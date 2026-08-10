'use strict';

const el = (id) => document.getElementById(id);
const SCREENS = ['login-screen', 'signup-screen', 'events-screen', 'event-screen',
  'profile-screen', 'help-screen'];

let session = null;   // the signed-in user and their trips
let current = null;   // the trip being viewed
let editingEventId = null;
let editingActivity = null;
let editingExpense = null;
let previousScreen = 'events-screen';

/* ------------------------------------------------------------- plumbing */

function show(id) {
  SCREENS.forEach((s) => { el(s).hidden = (s !== id); });
  const signedIn = !['login-screen', 'signup-screen'].includes(id);
  ['signout', 'profile-btn', 'help-btn'].forEach((b) => { el(b).hidden = !signedIn; });
  window.scrollTo(0, 0);
}

function money(amount, currency) {
  const value = Number(amount || 0).toLocaleString(undefined,
    { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  return `${currency || ''} ${value}`.trim();
}

function escapeHtml(text) {
  return String(text == null ? '' : text).replace(/[&<>"']/g, (c) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  }[c]));
}

function prettyDate(iso) {
  if (!iso || !/^\d{4}-\d{2}-\d{2}$/.test(iso)) { return iso || ''; }
  const [y, m, d] = iso.split('-').map(Number);
  return new Date(y, m - 1, d).toLocaleDateString('en-US',
    { month: 'short', day: 'numeric', year: 'numeric' });
}

function prettyTime(value) {
  if (!value || !/^\d{1,2}:\d{2}$/.test(value)) { return value || ''; }
  const [h, min] = value.split(':').map(Number);
  const hour = h % 12 === 0 ? 12 : h % 12;
  return `${hour}:${String(min).padStart(2, '0')} ${h >= 12 ? 'PM' : 'AM'}`;
}

async function api(path, options) {
  const response = await fetch(path, options);
  const data = await response.json().catch(() => ({}));
  if (!response.ok) { throw new Error(data.error || 'Something went wrong.'); }
  return data;
}

const post = (path, body) => api(path, {
  method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body)
});

function toast(message) {
  const box = el('toast');
  box.textContent = message;
  box.hidden = false;
  clearTimeout(toast.timer);
  toast.timer = setTimeout(() => { box.hidden = true; }, 2600);
}

function showError(id, message) {
  const box = el(id);
  box.textContent = message;
  box.hidden = false;
}

async function run(button, label, work) {
  const original = button.textContent;
  button.disabled = true;
  button.textContent = label;
  try {
    await work();
  } catch (e) {
    toast(e.message);
  } finally {
    button.disabled = false;
    button.textContent = original;
  }
}

function debounce(fn, wait) {
  let timer;
  return (...args) => { clearTimeout(timer); timer = setTimeout(() => fn(...args), wait); };
}

/* ------------------------------------------------------ login / signup */

document.querySelectorAll('.reveal').forEach((button) => {
  button.addEventListener('click', () => {
    const field = el(button.dataset.for);
    const hidden = field.type === 'password';
    field.type = hidden ? 'text' : 'password';
    button.textContent = hidden ? 'Hide' : 'Show';
  });
});

el('to-signup').addEventListener('click', () => show('signup-screen'));
el('to-login').addEventListener('click', () => show('login-screen'));
el('password').addEventListener('keydown', (e) => { if (e.key === 'Enter') { signIn(); } });
el('signin').addEventListener('click', signIn);

// Kept so the trip list can be refreshed after creating or deleting one.
let lastPassword = '';

async function signIn() {
  el('login-error').hidden = true;
  await run(el('signin'), 'Signing in…', async () => {
    try {
      lastPassword = el('password').value;
      session = await post('/api/login',
        { username: el('username').value, password: lastPassword });
      afterSignIn();
    } catch (e) {
      showError('login-error', e.message);
    }
  });
}

function afterSignIn() {
  renderEvents();
  show('events-screen');
  loadCurrencies();
}

async function refreshTrips() {
  session = await post('/api/login', { username: session.username, password: lastPassword });
  renderEvents();
}

// Live password strength, mirroring the desktop signup rules.
const RULES = [
  ['8+ characters', (p) => p.length >= 8],
  ['1 uppercase letter', (p) => /[A-Z]/.test(p)],
  ['1 lowercase letter', (p) => /[a-z]/.test(p)],
  ['1 number', (p) => /[0-9]/.test(p)],
  ['1 special character', (p) => /[^A-Za-z0-9]/.test(p)]
];

el('su-password').addEventListener('input', () => {
  const value = el('su-password').value;
  el('strength').hidden = value.length === 0;
  const met = RULES.filter(([, test]) => test(value)).length;
  document.querySelectorAll('#strength .bars i').forEach((bar, i) => {
    bar.className = met >= (i + 1) * 2 ? 'on' : '';
  });
  el('rules').innerHTML = RULES.map(([label, test]) =>
    `<li class="${test(value) ? 'met' : ''}">${test(value) ? '✓' : '○'} ${label}</li>`).join('');
});

el('signup').addEventListener('click', () => {
  el('signup-error').hidden = true;
  run(el('signup'), 'Creating…', async () => {
    try {
      await post('/api/signup', {
        username: el('su-username').value, displayName: el('su-display').value,
        email: el('su-email').value, password: el('su-password').value
      });
      lastPassword = el('su-password').value;
      session = await post('/api/login',
        { username: el('su-username').value, password: lastPassword });
      afterSignIn();
    } catch (e) {
      showError('signup-error', e.message);
    }
  });
});

el('signout').addEventListener('click', () => {
  session = null;
  lastPassword = '';
  el('password').value = '';
  show('login-screen');
});

el('help-btn').addEventListener('click', () => {
  previousScreen = SCREENS.find((s) => !el(s).hidden) || 'events-screen';
  show('help-screen');
});
el('profile-btn').addEventListener('click', openProfile);
document.querySelectorAll('[data-back]').forEach((b) =>
  b.addEventListener('click', () => show(previousScreen)));

/* --------------------------------------------------------------- trips */

function renderEvents() {
  el('greeting').textContent = `Hi ${session.displayName || session.username}`;
  const count = session.events.length;
  el('event-count').textContent = count === 1 ? '1 trip' : `${count} trips`;

  const list = el('event-list');
  list.innerHTML = '';
  if (!count) {
    list.innerHTML = '<p class="empty">No trips yet. Tap + to create one.</p>';
    return;
  }
  session.events.forEach((event) => {
    const row = document.createElement('div');
    row.className = 'event';
    row.innerHTML = `<strong>${escapeHtml(event.eventName)}</strong>` +
      `<span>${escapeHtml(prettyDate(event.dateInfo))}</span>`;
    row.addEventListener('click', () => openEvent(event.eventId));
    list.appendChild(row);
  });
}

el('new-event').addEventListener('click', () => {
  editingEventId = null;
  el('event-form-title').textContent = 'Create Event';
  ['ev-name', 'ev-desc', 'ev-loc', 'ev-budget', 'ev-start', 'ev-stime', 'ev-end', 'ev-etime']
    .forEach((id) => { el(id).value = ''; });
  el('event-form').hidden = !el('event-form').hidden;
});

el('edit-event').addEventListener('click', () => {
  editingEventId = current.eventId;
  el('event-form-title').textContent = 'Edit Event';
  el('ev-name').value = current.eventName || '';
  el('ev-desc').value = current.description || '';
  el('ev-loc').value = current.location || '';
  el('ev-budget').value = current.budget || '';
  el('ev-start').value = current.startDate || '';
  el('ev-stime').value = current.startTime || '';
  el('ev-end').value = current.endDate || '';
  el('ev-etime').value = current.endTime || '';
  el('event-form').hidden = false;
  show('events-screen');
});

el('ev-save').addEventListener('click', () => {
  el('ev-error').hidden = true;
  run(el('ev-save'), 'Saving…', async () => {
    const body = {
      username: session.username, eventId: editingEventId,
      name: el('ev-name').value, description: el('ev-desc').value,
      location: el('ev-loc').value, budget: parseFloat(el('ev-budget').value) || 0,
      currency: el('ev-currency').value || 'CAD',
      startDate: el('ev-start').value, startTime: el('ev-stime').value,
      endDate: el('ev-end').value, endTime: el('ev-etime').value
    };
    try {
      await post(editingEventId ? '/api/trip/edit' : '/api/trip/create', body);
      el('event-form').hidden = true;
      await refreshTrips();
      toast(editingEventId ? 'Trip updated.' : 'Trip created.');
    } catch (e) {
      showError('ev-error', e.message);
    }
  });
});

el('delete-event').addEventListener('click', () => {
  if (!window.confirm('Delete this trip? This cannot be undone.')) { return; }
  run(el('delete-event'), 'Deleting…', async () => {
    await post('/api/trip/delete', { eventId: current.eventId, username: session.username });
    await refreshTrips();
    show('events-screen');
    toast('Trip deleted.');
  });
});

/* --------------------------------------------------------------- event */

el('back').addEventListener('click', () => show('events-screen'));

async function openEvent(eventId) {
  show('event-screen');
  el('event-name').textContent = 'Loading…';
  ['event-description', 'event-location', 'event-dates']
    .forEach((id) => { el(id).textContent = ''; });
  ['stats', 'expense-list', 'activity-list', 'balance-list', 'attendee-list']
    .forEach((id) => { el(id).innerHTML = ''; });
  ['add-form', 'act-form', 'guest-form'].forEach((id) => { el(id).hidden = true; });

  try {
    renderEvent(await api(`/api/event?id=${encodeURIComponent(eventId)}`));
  } catch (e) {
    el('event-name').textContent = 'Could not load that trip';
    el('event-description').textContent = e.message;
  }
}

function renderEvent(event) {
  current = event;
  const currency = event.currency;

  el('event-name').textContent = event.eventName;
  el('event-description').textContent = event.description || '';
  el('event-location').textContent = event.location ? `Location: ${event.location}` : '';
  el('event-dates').textContent = event.startDate
    ? `Date: ${prettyDate(event.startDate)}` +
      `${event.endDate ? ` to ${prettyDate(event.endDate)}` : ''}` : '';

  const remaining = (event.budget || 0) - (event.spent || 0);
  const owing = event.balances.filter((b) => balanceWording(b.status) === 'Owes').length;
  const percentLeft = event.budget ? Math.round((remaining / event.budget) * 100) : 0;
  const count = event.expenses.length;
  el('stats').innerHTML = `
    <div class="stat"><span>TOTAL BUDGET</span><b>${money(event.budget, currency)}</b>
      <em>${event.budget ? 'Event budget' : 'No budget set'}</em></div>
    <div class="stat"><span>TOTAL SPENT</span><b>${money(event.spent, currency)}</b>
      <em>${count} ${count === 1 ? 'expense' : 'expenses'}</em></div>
    <div class="stat"><span>REMAINING</span>
      <b class="${remaining < 0 ? 'owes' : 'gets'}">${money(remaining, currency)}</b>
      <em>${percentLeft < 0 ? `${Math.abs(percentLeft)}% over budget`
        : `${percentLeft}% of budget left`}</em></div>
    <div class="stat"><span>UNSETTLED DEBTS</span><b>${money(sumOwed(event), currency)}</b>
      <em>${owing === 1 ? '1 person owes' : `${owing} people owe`}</em></div>`;

  renderAttendees(event);
  renderActivities(event);
  renderExpenses(event);
  renderAddForm(event);

  el('balance-list').innerHTML = event.balances.length
    ? event.balances.map((balance) => {
        const wording = balanceWording(balance.status);
        return `<div class="row"><div>${escapeHtml(balance.name)}</div>
            <div class="amount ${wording === 'Owes' ? 'owes' : 'gets'}">` +
            `${wording} ${money(Math.abs(balance.amount), currency)}</div></div>`;
      }).join('')
    : '<p class="empty">Everyone is settled up.</p>';

  loadPanel('weather', '/api/weather', renderWeather);
  loadPanel('insight', '/api/insight', renderInsight);
  el('itinerary-list').innerHTML = '<p class="empty">Tap Load to build the timeline.</p>';
}

function balanceWording(status) {
  const text = String(status).toLowerCase();
  return text.includes('owe') && !text.includes('is_owed') ? 'Owes' : 'Gets back';
}

function sumOwed(event) {
  return event.balances.filter((b) => balanceWording(b.status) === 'Owes')
    .reduce((total, b) => total + Math.abs(b.amount), 0);
}

// The image goes inside the circle rather than being the circle, so the shared
// `.avatar img` rule can crop it with object-fit instead of squashing it.
function avatarHtml(picture, name) {
  return picture
    ? `<span class="avatar small"><img src="data:image/png;base64,${picture}" alt=""></span>`
    : `<span class="avatar small initial">${escapeHtml((name || '?')[0].toUpperCase())}</span>`;
}

/* ----------------------------------------------------------- attendees */

async function renderAttendees(event) {
  const list = el('attendee-list');
  try {
    const data = await api(`/api/trip/attendees?id=${event.eventId}`);
    list.innerHTML = data.attendees.map((person) => `
      <div class="row">
        <div class="person">${avatarHtml(person.picture, person.username)}
          <div><div>${escapeHtml(person.displayName || person.username)}</div>
            <div class="who">${escapeHtml(person.username)}</div></div></div>
        <button class="pay danger-text" data-guest="${escapeHtml(person.username)}">Remove</button>
      </div>`).join('') || '<p class="empty">Nobody added yet.</p>';

    list.querySelectorAll('[data-guest]').forEach((button) => {
      button.addEventListener('click', () => run(button, '…', async () => {
        renderEvent(await post('/api/trip/guest/remove',
          { eventId: event.eventId, username: button.dataset.guest }));
        toast('Attendee removed.');
      }));
    });
  } catch (e) {
    list.innerHTML = `<p class="empty">${escapeHtml(e.message)}</p>`;
  }
}

el('guest-toggle').addEventListener('click', () => {
  const form = el('guest-form');
  form.hidden = !form.hidden;
  el('guest-toggle').textContent = form.hidden ? '+' : '×';
});

el('guest-name').addEventListener('input', debounce(async () => {
  const query = el('guest-name').value.trim();
  const box = el('guest-results');
  if (query.length < 2) { box.innerHTML = ''; return; }
  try {
    const data = await api(`/api/users/search?q=${encodeURIComponent(query)}` +
      `&username=${encodeURIComponent(session.username)}`);
    box.innerHTML = data.users.map((u) =>
      `<button class="chip" data-add="${escapeHtml(u.username)}">${escapeHtml(u.username)}</button>`)
      .join('') || '<p class="empty">No matches.</p>';

    box.querySelectorAll('[data-add]').forEach((chip) => {
      chip.addEventListener('click', () => run(chip, '…', async () => {
        try {
          renderEvent(await post('/api/trip/guest/add',
            { eventId: current.eventId, username: chip.dataset.add }));
          el('guest-name').value = '';
          box.innerHTML = '';
          el('guest-form').hidden = true;
          el('guest-toggle').textContent = '+';
          toast('Attendee added.');
        } catch (e) {
          showError('guest-error', e.message);
        }
      }));
    });
  } catch (e) {
    box.innerHTML = `<p class="empty">${escapeHtml(e.message)}</p>`;
  }
}, 300));

/* ---------------------------------------------------------- activities */

function renderActivities(event) {
  const list = el('activity-list');
  list.innerHTML = event.activities.length
    ? event.activities.map((activity) => `
        <div class="row">
          <div><div>${escapeHtml(activity.name)}</div>
            <div class="who">${escapeHtml(activity.location || '')}</div></div>
          <div class="amount">${escapeHtml(prettyDate(activity.date))}` +
            `<small>${escapeHtml(prettyTime(activity.time))}</small></div>
          <div class="rowbtns">
            <button class="pay" data-edit-act="${activity.index}">Edit</button>
            <button class="pay danger-text" data-del-act="${activity.index}">Remove</button>
          </div>
        </div>`).join('')
    : '<p class="empty">Nothing scheduled yet.</p>';

  list.querySelectorAll('[data-edit-act]').forEach((button) => {
    button.addEventListener('click', () => {
      const activity = event.activities.find((a) => String(a.index) === button.dataset.editAct);
      editingActivity = activity.index;
      el('act-name').value = activity.name;
      el('act-date').value = activity.date || '';
      el('act-time').value = activity.time || '';
      el('act-loc').value = activity.location || '';
      el('act-form').hidden = false;
      el('act-toggle').textContent = '×';
    });
  });

  list.querySelectorAll('[data-del-act]').forEach((button) => {
    button.addEventListener('click', () => run(button, '…', async () => {
      renderEvent(await post('/api/trip/activity/remove',
        { eventId: event.eventId, index: Number(button.dataset.delAct) }));
      toast('Activity removed.');
    }));
  });
}

el('act-toggle').addEventListener('click', () => {
  const form = el('act-form');
  form.hidden = !form.hidden;
  editingActivity = null;
  ['act-name', 'act-date', 'act-time', 'act-loc'].forEach((id) => { el(id).value = ''; });
  el('act-toggle').textContent = form.hidden ? '+' : '×';
});

el('act-save').addEventListener('click', () => {
  el('act-error').hidden = true;
  run(el('act-save'), 'Saving…', async () => {
    const body = {
      eventId: current.eventId, index: editingActivity,
      name: el('act-name').value, date: el('act-date').value,
      time: el('act-time').value, location: el('act-loc').value
    };
    try {
      const updated = await post(
        editingActivity === null ? '/api/trip/activity/add' : '/api/trip/activity/edit', body);
      el('act-form').hidden = true;
      el('act-toggle').textContent = '+';
      renderEvent(updated);
      toast(editingActivity === null ? 'Activity added.' : 'Activity updated.');
    } catch (e) {
      showError('act-error', e.message);
    }
  });
});

/* ------------------------------------------------------------ expenses */

function renderExpenses(event) {
  const list = el('expense-list');
  list.innerHTML = '';
  if (!event.expenses.length) {
    list.innerHTML = '<p class="empty">No expenses yet.</p>';
    return;
  }

  event.expenses.forEach((expense) => {
    const paid = String(expense.status).toUpperCase() === 'PAID';
    const converted = expense.originalCurrency && expense.originalCurrency !== event.currency
      ? `<small>${money(expense.originalAmount, expense.originalCurrency)}</small>` : '';

    const row = document.createElement('div');
    row.className = 'row';
    row.innerHTML = `
      <div><div>${escapeHtml(expense.name)}</div>
        <div class="who">${escapeHtml(expense.payer)} · split ${expense.debtorCount} ways` +
        `${expense.customSplit ? ' · custom' : ''}</div>
        <span class="pill ${paid ? 'paid' : 'unpaid'}">${paid ? 'Paid' : 'Unpaid'}</span></div>
      <div class="amount">${money(expense.amount, event.currency)}${converted}</div>`;

    const buttons = document.createElement('div');
    buttons.className = 'rowbtns';

    if (!paid) {
      const settle = document.createElement('button');
      settle.className = 'pay';
      settle.textContent = 'Settle';
      settle.addEventListener('click', () => run(settle, '…', async () => {
        renderEvent(await post('/api/expense/pay',
          { eventId: event.eventId, expenseId: expense.id }));
        toast('Expense settled.');
      }));
      buttons.appendChild(settle);
    }

    const edit = document.createElement('button');
    edit.className = 'pay';
    edit.textContent = 'Edit';
    edit.addEventListener('click', () => {
      editingExpense = expense;
      el('ex-form-title').textContent = 'Edit expense';
      el('ex-save').textContent = 'Save changes';
      el('ex-name').value = expense.name;
      el('ex-amount').value = expense.amount;
      el('ex-payer').value = expense.payer;
      el('add-form').hidden = false;
      el('add-toggle').textContent = '×';
      el('add-form').scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    });
    buttons.appendChild(edit);

    const remove = document.createElement('button');
    remove.className = 'pay danger-text';
    remove.textContent = 'Remove';
    remove.addEventListener('click', () => run(remove, '…', async () => {
      renderEvent(await post('/api/trip/expense/remove',
        { eventId: event.eventId, expenseId: expense.id }));
      toast('Expense removed.');
    }));
    buttons.appendChild(remove);

    row.appendChild(buttons);
    list.appendChild(row);
  });
}

function renderAddForm(event) {
  el('ex-payer').innerHTML = event.attendees
    .map((name) => `<option value="${escapeHtml(name)}">${escapeHtml(name)}</option>`).join('');

  const chips = el('ex-debtors');
  chips.innerHTML = '';
  event.attendees.forEach((name) => {
    const chip = document.createElement('button');
    chip.type = 'button';
    chip.className = 'chip on';
    chip.textContent = name;
    chip.dataset.name = name;
    chip.addEventListener('click', () => chip.classList.toggle('on'));
    chips.appendChild(chip);
  });
}

el('add-toggle').addEventListener('click', () => {
  const form = el('add-form');
  form.hidden = !form.hidden;
  editingExpense = null;
  el('ex-form-title').textContent = 'Add expense';
  el('ex-save').textContent = 'Add expense';
  el('ex-name').value = '';
  el('ex-amount').value = '';
  el('add-toggle').textContent = form.hidden ? '+' : '×';
});

el('ex-save').addEventListener('click', () => {
  el('ex-error').hidden = true;
  run(el('ex-save'), 'Adding…', async () => {
    try {
      const body = {
        eventId: current.eventId, name: el('ex-name').value,
        amount: parseFloat(el('ex-amount').value), payer: el('ex-payer').value,
        debtors: [...document.querySelectorAll('#ex-debtors .chip.on')].map((c) => c.dataset.name)
      };
      if (editingExpense) {
        body.expenseId = editingExpense.id;
        body.status = editingExpense.status;
      }
      const updated = await post(
        editingExpense ? '/api/trip/expense/edit' : '/api/expense/add', body);
      el('ex-name').value = '';
      el('ex-amount').value = '';
      el('add-form').hidden = true;
      el('add-toggle').textContent = '+';
      renderEvent(updated);
      toast(editingExpense ? 'Expense updated.' : 'Expense added.');
      editingExpense = null;
    } catch (e) {
      showError('ex-error', e.message);
    }
  });
});

/* ----------------------------------- weather, insights and itinerary */

async function loadPanel(id, path, render) {
  const target = el(id);
  target.innerHTML = '<p class="empty">Loading…</p>';
  try {
    const data = await api(`${path}?id=${encodeURIComponent(current.eventId)}`);
    if (data.error) {
      target.innerHTML = `<p class="empty">${escapeHtml(data.error)}</p>`;
    } else {
      render(target, data);
    }
  } catch (e) {
    target.innerHTML = `<p class="empty">${escapeHtml(e.message)}</p>`;
  }
}

function renderWeather(target, data) {
  const days = data.forecast.map((day) => `
    <div class="day"><span>${escapeHtml(prettyDate(day.date).replace(/,.*$/, ''))}</span>
      <b>${Math.round(day.temperature)} C</b><em>${escapeHtml(day.status)}</em></div>`).join('');
  target.innerHTML = `
    <div class="temp"><b>${Math.round(data.temperature)} C</b>
      <span>${escapeHtml(data.status)}</span></div>
    <p class="detail" style="padding-left:0">Precipitation: ${Math.round(data.precipitation)}%
      | Wind: ${Math.round(data.wind)} km/h</p>
    <div class="forecast">${days}</div>`;
}

function renderInsight(target, data) {
  const rows = [['Fun factor', data.fun], ['Safety', data.safety],
    ['Accessibility', data.accessibility], ['Nearby amenities', data.amenities],
    ['Affordability', data.affordability]]
    .map(([label, score]) => `<div class="score"><span>${label}</span><b>${score} / 5</b></div>`)
    .join('');
  const tags = data.tags.map((t) => `<span class="tag">${escapeHtml(t)}</span>`).join('');
  target.innerHTML = rows + (tags ? `<div class="tags">${tags}</div>` : '');
}

function renderItinerary(target, data) {
  if (!data.items.length) {
    target.innerHTML = '<p class="empty">Nothing scheduled yet.</p>';
    return;
  }
  target.innerHTML = data.items.map((item) => {
    const travel = item.travel
      ? `<div class="travel${/tight|not enough/.test(item.travel) ? ' warn' : ''}">` +
        `${escapeHtml(item.travel)}</div>` : '';
    return `<div class="row">
        <div><div>${escapeHtml(item.name)}</div>
          <div class="who">${escapeHtml(item.location || '')}</div></div>
        <div class="amount">${escapeHtml(prettyDate(item.date))}` +
        `<small>${escapeHtml(prettyTime(item.time))}</small></div>
      </div>${travel}`;
  }).join('');
}

el('itin-load').addEventListener('click', () =>
  loadPanel('itinerary-list', '/api/itinerary', renderItinerary));

/* ------------------------------------------------------------- profile */

async function openProfile() {
  previousScreen = SCREENS.find((s) => !el(s).hidden) || 'events-screen';
  show('profile-screen');
  el('ac-msg').hidden = true;

  const data = await api(`/api/profile?username=${encodeURIComponent(session.username)}` +
    `&viewer=${encodeURIComponent(session.username)}`);
  el('pf-username').textContent = data.username;
  el('pf-counts').textContent = `${data.followers} followers · ${data.following} following`;
  el('pf-bio').value = data.bio || '';
  el('avatar').innerHTML = data.picture
    ? `<img src="data:image/png;base64,${data.picture}" alt="">`
    : `<span class="initial">${escapeHtml(data.username[0].toUpperCase())}</span>`;

  el('ac-display').value = session.displayName || '';
  el('ac-username').value = session.username;
  loadConnections(false);
  loadCurrencies();
}

el('pf-photo').addEventListener('change', async () => {
  const file = el('pf-photo').files[0];
  if (!file) { return; }
  const buffer = await file.arrayBuffer();
  let binary = '';
  new Uint8Array(buffer).forEach((byte) => { binary += String.fromCharCode(byte); });
  await post('/api/profile/update',
    { username: session.username, bio: el('pf-bio').value, picture: btoa(binary) });
  toast('Photo updated.');
  openProfile();
});

el('pf-save').addEventListener('click', () => run(el('pf-save'), 'Saving…', async () => {
  await post('/api/profile/update', { username: session.username, bio: el('pf-bio').value });
  toast('Profile saved.');
}));

el('find-user').addEventListener('input', debounce(async () => {
  const query = el('find-user').value.trim();
  const box = el('find-results');
  if (query.length < 2) { box.innerHTML = ''; return; }

  const data = await api(`/api/users/search?q=${encodeURIComponent(query)}` +
    `&username=${encodeURIComponent(session.username)}`);
  box.innerHTML = data.users.map((u) => `
    <div class="row"><div>${escapeHtml(u.username)}</div>
      <button class="pay" data-follow="${escapeHtml(u.username)}" data-on="${u.isFollowing}">
        ${u.isFollowing ? 'Unfollow' : 'Follow'}</button></div>`).join('')
    || '<p class="empty">No matches.</p>';

  box.querySelectorAll('[data-follow]').forEach((button) => {
    button.addEventListener('click', () => run(button, '…', async () => {
      await post('/api/follow', {
        currentUsername: session.username, targetUsername: button.dataset.follow,
        follow: button.dataset.on !== 'true'
      });
      el('find-user').dispatchEvent(new Event('input'));
      loadConnections(el('tab-followers').classList.contains('on'));
    }));
  });
}, 300));

el('tab-following').addEventListener('click', () => loadConnections(false));
el('tab-followers').addEventListener('click', () => loadConnections(true));

async function loadConnections(followers) {
  el('tab-followers').classList.toggle('on', followers);
  el('tab-following').classList.toggle('on', !followers);
  el('conn-title').textContent = followers ? 'Followers' : 'Following';

  const box = el('conn-list');
  box.innerHTML = '<p class="empty">Loading…</p>';
  const data = await api(`/api/follows?username=${encodeURIComponent(session.username)}` +
    `&followers=${followers}`);

  box.innerHTML = data.people.map((person) => `
    <div class="row">
      <div class="person">${avatarHtml(person.picture, person.username)}
        <div>${escapeHtml(person.username)}</div></div>
      ${followers ? ''
        : `<button class="pay danger-text" data-unfollow="${escapeHtml(person.username)}">Unfollow</button>`}
    </div>`).join('')
    || `<p class="empty">${followers ? 'No followers yet.' : 'Not following anyone yet.'}</p>`;

  box.querySelectorAll('[data-unfollow]').forEach((button) => {
    button.addEventListener('click', () => run(button, '…', async () => {
      await post('/api/follow', {
        currentUsername: session.username, targetUsername: button.dataset.unfollow, follow: false
      });
      loadConnections(false);
    }));
  });
}

/* ------------------------------------------------------------ currency */

let currencyOptions = null;

async function loadCurrencies() {
  if (currencyOptions) { return; }
  try {
    const data = await api('/api/currencies');
    currencyOptions = data.currencies;
    const html = currencyOptions
      .map((c) => `<option value="${c.code}">${c.code} | ${escapeHtml(c.name)}</option>`).join('');
    el('pref-currency').innerHTML = html;
    el('ev-currency').innerHTML = html;
    el('pref-currency').value = session.preferredCurrency || 'CAD';
    el('ev-currency').value = session.preferredCurrency || 'CAD';
  } catch (e) {
    const fallback = '<option value="CAD">CAD | Canadian Dollar</option>';
    el('pref-currency').innerHTML = fallback;
    el('ev-currency').innerHTML = fallback;
  }
}

el('pref-currency').addEventListener('change', async () => {
  try {
    await post('/api/account/currency',
      { username: session.username, currency: el('pref-currency').value });
    session.preferredCurrency = el('pref-currency').value;
    toast(`Preferred currency changed to ${el('pref-currency').value}.`);
    if (current) { renderEvent(await api(`/api/event?id=${current.eventId}`)); }
  } catch (e) {
    toast(e.message);
  }
});

/* ---------------------------------------------------- account settings */

function accountAction(buttonId, path, buildBody, onDone) {
  el(buttonId).addEventListener('click', () => run(el(buttonId), 'Saving…', async () => {
    el('ac-msg').hidden = true;
    try {
      await post(path, buildBody());
      if (onDone) { onDone(); }
      toast('Saved.');
    } catch (e) {
      showError('ac-msg', e.message);
    }
  }));
}

accountAction('ac-display-save', '/api/account/displayname',
  () => ({ username: session.username, displayName: el('ac-display').value }),
  () => { session.displayName = el('ac-display').value; });

accountAction('ac-username-save', '/api/account/username',
  () => ({ oldUsername: session.username, newUsername: el('ac-username').value }),
  () => { session.username = el('ac-username').value; openProfile(); });

accountAction('ac-address-save', '/api/account/address',
  () => ({ username: session.username, address: el('ac-address').value }));

accountAction('ac-password-save', '/api/account/password',
  () => ({
    username: session.username, oldPassword: el('ac-old').value,
    newPassword: el('ac-new').value, confirmPassword: el('ac-confirm').value
  }),
  () => {
    lastPassword = el('ac-new').value;
    ['ac-old', 'ac-new', 'ac-confirm'].forEach((id) => { el(id).value = ''; });
  });

/* ------------------------------------------ map, photo and the report */

el('map-load').addEventListener('click', () => loadPanel('map-box', '/api/trip/map', renderMap));

function renderMap(target, data) {
  if (!data.points.length) {
    target.innerHTML = '<p class="empty">No locations to plot yet.</p>';
    return;
  }

  // OpenStreetMap's embed takes a bounding box, so fit one around every point.
  const lats = data.points.map((p) => p.latitude);
  const lons = data.points.map((p) => p.longitude);
  const pad = 0.02;
  const box = [Math.min(...lons) - pad, Math.min(...lats) - pad,
    Math.max(...lons) + pad, Math.max(...lats) + pad].join(',');
  const marker = `${data.points[0].latitude},${data.points[0].longitude}`;

  target.innerHTML = `
    <iframe title="Event map" loading="lazy"
      src="https://www.openstreetmap.org/export/embed.html?bbox=${box}&layer=mapnik&marker=${marker}"></iframe>
    <div class="maplist">${data.points.map((point) => `
      <div class="row"><div><div>${escapeHtml(point.title)}</div>
        <div class="who">${escapeHtml(point.address || '')}</div></div>
        <div class="amount"><small>${point.isEvent ? 'Event' : 'Activity'}</small></div></div>`).join('')}
    </div>`;
}

el('ev-photo').addEventListener('change', async () => {
  const file = el('ev-photo').files[0];
  if (!file) { return; }
  try {
    const buffer = await file.arrayBuffer();
    let binary = '';
    new Uint8Array(buffer).forEach((byte) => { binary += String.fromCharCode(byte); });
    renderEvent(await post('/api/trip/photo',
      { eventId: current.eventId, photo: btoa(binary) }));
    toast('Event photo saved.');
  } catch (e) {
    toast(e.message);
  }
});

el('ev-report').addEventListener('click', () => {
  // Let the browser download it rather than writing to a path on the server.
  window.location.href = `/api/trip/report?id=${current.eventId}` +
    `&username=${encodeURIComponent(session.username)}`;
});
