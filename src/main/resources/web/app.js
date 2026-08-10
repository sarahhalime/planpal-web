'use strict';

const el = (id) => document.getElementById(id);
const screens = ['login-screen', 'events-screen', 'event-screen'];

let session = null;
let current = null;

function show(id) {
  screens.forEach((s) => { el(s).hidden = (s !== id); });
  el('signout').hidden = (id === 'login-screen');
  window.scrollTo(0, 0);
}

function money(amount, currency) {
  const value = Number(amount || 0).toLocaleString(undefined, {
    minimumFractionDigits: 2, maximumFractionDigits: 2
  });
  return `${currency || ''} ${value}`.trim();
}

function escapeHtml(text) {
  return String(text == null ? '' : text).replace(/[&<>"']/g, (c) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  }[c]));
}

async function api(path, options) {
  const response = await fetch(path, options);
  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(data.error || 'Something went wrong.');
  }
  return data;
}

/* ---------------------------------------------------------------- login */

el('reveal').addEventListener('click', () => {
  const field = el('password');
  const hidden = field.type === 'password';
  field.type = hidden ? 'text' : 'password';
  el('reveal').textContent = hidden ? 'Hide' : 'Show';
  el('reveal').setAttribute('aria-label', hidden ? 'Hide password' : 'Show password');
});

el('password').addEventListener('keydown', (e) => {
  if (e.key === 'Enter') { signIn(); }
});

el('signin').addEventListener('click', signIn);

async function signIn() {
  const button = el('signin');
  const error = el('login-error');

  error.hidden = true;
  button.disabled = true;
  button.textContent = 'Signing in…';

  try {
    session = await api('/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: el('username').value, password: el('password').value })
    });
    renderEvents();
    show('events-screen');
  } catch (e) {
    error.textContent = e.message;
    error.hidden = false;
  } finally {
    button.disabled = false;
    button.textContent = 'Log In';
  }
}

el('signout').addEventListener('click', () => {
  session = null;
  el('password').value = '';
  show('login-screen');
});

/* --------------------------------------------------------------- events */

function renderEvents() {
  el('greeting').textContent = `Hi ${session.displayName || session.username}`;
  const count = session.events.length;
  el('event-count').textContent = count === 1 ? '1 trip' : `${count} trips`;

  const list = el('event-list');
  list.innerHTML = '';

  if (!count) {
    list.innerHTML = '<p class="empty">No trips yet.</p>';
    return;
  }

  session.events.forEach((event) => {
    const button = document.createElement('button');
    button.className = 'event';
    button.innerHTML =
      `<strong>${escapeHtml(event.eventName)}</strong><span>${escapeHtml(event.dateInfo || '')}</span>`;
    button.addEventListener('click', () => openEvent(event.eventId));
    list.appendChild(button);
  });
}

/* ---------------------------------------------------------------- event */

el('back').addEventListener('click', () => show('events-screen'));

document.querySelectorAll('.tab').forEach((tab) => {
  tab.addEventListener('click', () => {
    document.querySelectorAll('.tab').forEach((t) => t.classList.remove('active'));
    tab.classList.add('active');
    ['expenses', 'activities', 'balances'].forEach((name) => {
      el('tab-' + name).hidden = (name !== tab.dataset.tab);
    });
  });
});

async function openEvent(eventId) {
  show('event-screen');
  el('event-name').textContent = 'Loading…';
  el('event-meta').textContent = '';
  el('stats').innerHTML = '';
  // Clear the lists, not the whole panel: the add-expense form lives in there too.
  el('expense-list').innerHTML = '';
  el('tab-activities').innerHTML = '';
  el('tab-balances').innerHTML = '';
  el('add-form').hidden = true;
  el('add-toggle').textContent = '+ Add expense';

  try {
    renderEvent(await api(`/api/event?id=${encodeURIComponent(eventId)}`));
  } catch (e) {
    el('event-name').textContent = 'Could not load that trip';
    el('event-meta').textContent = e.message;
  }
}

function renderEvent(event) {
  current = event;
  const currency = event.currency;

  el('event-name').textContent = event.eventName;
  el('event-meta').textContent =
    [event.location, event.startDate && `${event.startDate} → ${event.endDate || ''}`.trim()]
      .filter(Boolean).join(' · ');

  const remaining = (event.budget || 0) - (event.spent || 0);
  el('stats').innerHTML = `
    <div class="stat"><span>Budget</span><b>${money(event.budget, currency)}</b></div>
    <div class="stat"><span>Spent</span><b>${money(event.spent, currency)}</b></div>
    <div class="stat"><span>Remaining</span><b class="${remaining < 0 ? 'owes' : 'gets'}">${money(remaining, currency)}</b></div>
    <div class="stat"><span>People</span><b>${event.attendees.length}</b></div>`;

  renderExpenses(event);
  renderAddForm(event);

  el('tab-activities').innerHTML = event.activities.length
    ? event.activities.map((activity) => `<div class="row">
          <div>
            <div>${escapeHtml(activity.name)}</div>
            <div class="who">${escapeHtml(activity.location || '')}</div>
          </div>
          <div class="amount">${escapeHtml(activity.date || '')}<small>${escapeHtml(activity.time || '')}</small></div>
        </div>`).join('')
    : '<p class="empty">Nothing scheduled yet.</p>';

  el('tab-balances').innerHTML = event.balances.length
    ? event.balances.map((balance) => {
        const owes = String(balance.status).toLowerCase().includes('owe');
        return `<div class="row">
            <div>${escapeHtml(balance.name)}</div>
            <div class="amount ${owes ? 'owes' : 'gets'}">${money(Math.abs(balance.amount), currency)}
              <small>${escapeHtml(balance.status)}</small></div>
          </div>`;
      }).join('')
    : '<p class="empty">Everyone is settled up.</p>';
}

/* ------------------------------------------------------- expenses: write */

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
      <div>
        <div>${escapeHtml(expense.name)}</div>
        <div class="who">${escapeHtml(expense.payer)} · split ${expense.debtorCount} ways${expense.customSplit ? ' · custom' : ''}</div>
        <span class="pill ${paid ? 'paid' : 'unpaid'}">${paid ? 'Paid' : 'Unpaid'}</span>
      </div>
      <div class="amount">${money(expense.amount, event.currency)}${converted}</div>`;

    if (!paid) {
      const pay = document.createElement('button');
      pay.className = 'pay';
      pay.textContent = 'Settle';
      pay.addEventListener('click', async () => {
        pay.disabled = true;
        pay.textContent = '…';
        try {
          renderEvent(await api('/api/expense/pay', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ eventId: event.eventId, expenseId: expense.id })
          }));
        } catch (e) {
          pay.disabled = false;
          pay.textContent = 'Settle';
          alert(e.message);
        }
      });
      row.appendChild(pay);
    }
    list.appendChild(row);
  });
}

function renderAddForm(event) {
  const payer = el('ex-payer');
  payer.innerHTML = event.attendees
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
  el('add-toggle').textContent = form.hidden ? '+ Add expense' : 'Cancel';
});

el('ex-save').addEventListener('click', async () => {
  const button = el('ex-save');
  const error = el('ex-error');
  const debtors = [...document.querySelectorAll('#ex-debtors .chip.on')].map((c) => c.dataset.name);

  error.hidden = true;
  button.disabled = true;
  button.textContent = 'Adding…';

  try {
    const updated = await api('/api/expense/add', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        eventId: current.eventId,
        name: el('ex-name').value,
        amount: parseFloat(el('ex-amount').value),
        payer: el('ex-payer').value,
        debtors
      })
    });
    el('ex-name').value = '';
    el('ex-amount').value = '';
    el('add-form').hidden = true;
    el('add-toggle').textContent = '+ Add expense';
    renderEvent(updated);
  } catch (e) {
    error.textContent = e.message;
    error.hidden = false;
  } finally {
    button.disabled = false;
    button.textContent = 'Add expense';
  }
});
