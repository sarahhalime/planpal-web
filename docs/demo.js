'use strict';

/*
 * Static demo shim.
 *
 * GitHub Pages serves files, it cannot run the Java server, so there is nothing to answer
 * the app's /api calls. This intercepts fetch and replies from a snapshot captured from the
 * real server instead, which lets the identical front end run unchanged: app.js is copied
 * from the application, not rewritten for the demo.
 *
 * Reads are answered from the snapshot. Writes cannot be honoured without the interactors
 * behind them, so they report that plainly rather than pretending to succeed.
 */

(function () {
  const READ_ONLY = 'This is a read-only demo. Run the app to add or change anything.';
  let snapshot = null;

  // Pages serves the snapshot with a ten minute cache, so a browser that loaded an older
  // copy keeps showing it after a redeploy. Revalidating means the page always reflects
  // what was last published.
  const loaded = fetch('demo-snapshot.json', { cache: 'no-cache' })
    .then((r) => r.json())
    .then((data) => { snapshot = data; });

  function reply(body, ok = true) {
    return new Response(JSON.stringify(body), {
      status: ok ? 200 : 400,
      headers: { 'Content-Type': 'application/json' }
    });
  }

  function idOf(url) {
    return new URL(url, window.location.href).searchParams.get('id');
  }

  function answer(url, options) {
    const path = new URL(url, window.location.href).pathname.replace(/^.*\/api\//, '/api/');
    const write = options && options.method === 'POST';
    const events = snapshot.events;

    if (path === '/api/login') {
      return reply(snapshot.login);
    }
    if (path === '/api/event') {
      const found = events[idOf(url)];
      return found ? reply(found.event) : reply({ error: 'Unknown trip.' }, false);
    }
    if (path === '/api/trip/attendees') {
      const found = events[idOf(url)];
      return reply(found ? found.attendees : { attendees: [] });
    }
    if (path === '/api/weather' || path === '/api/insight' || path === '/api/itinerary') {
      const name = path.split('/').pop();
      const found = events[idOf(url)];
      return reply((found && found[name])
        || { error: 'Only the Montreal trip has this in the demo.' });
    }
    if (path === '/api/trip/map') {
      const found = events[idOf(url)];
      return reply((found && found.map) || { error: 'Only the Montreal trip has a map here.' });
    }
    if (path === '/api/profile') {
      return reply(snapshot.profile);
    }
    if (path === '/api/follows') {
      const followers = new URL(url, window.location.href).searchParams.get('followers') === 'true';
      return reply(followers ? snapshot.followers : snapshot.following);
    }
    if (path === '/api/users/search') {
      return reply({ users: [] });
    }
    if (path === '/api/currencies') {
      return reply({ currencies: [{ code: 'CAD', name: 'Canadian Dollar' }] });
    }
    if (write) {
      return reply({ error: READ_ONLY }, false);
    }
    return reply({ error: READ_ONLY }, false);
  }

  const realFetch = window.fetch.bind(window);
  window.fetch = async function (url, options) {
    const target = typeof url === 'string' ? url : url.url;

    if (!target.includes('/api/')) {
      return realFetch(url, options);
    }
    await loaded;
    return answer(target, options);
  };

  // Sign-in is pre-filled so the demo opens in one tap.
  window.addEventListener('DOMContentLoaded', () => {
    document.getElementById('username').value = 'sarah';
    document.getElementById('password').value = 'demo1234';

    const banner = document.createElement('div');
    banner.className = 'demo-banner';
    banner.textContent = 'Read-only demo · data captured from the running app';
    document.getElementById('main').prepend(banner);
  });
}());
