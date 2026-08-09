const API_BASE = "http://localhost:8083";
let token = null;
let stompClient = null;
let currentBoardId = null;
let draggedCardId = null;

const loginBtn = document.getElementById("loginBtn");
const connectBtn = document.getElementById("connectBtn");
const statusEl = document.getElementById("status");
const feedEl = document.getElementById("feed");
const boardEl = document.getElementById("board");
const loginResultEl = document.getElementById("loginResult");

function authHeaders(extra = {}) {
  return { "Authorization": "Bearer " + token, ...extra };
}

loginBtn.addEventListener("click", async () => {
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;
  try {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password })
    });
    if (!res.ok) throw new Error("Login failed: " + res.status);
    const data = await res.json();
    token = data.token;
    loginResultEl.textContent = `Logged in as ${data.name} (id: ${data.id})`;
    connectBtn.disabled = false;
  } catch (err) {
    loginResultEl.textContent = "Error: " + err.message;
  }
});

connectBtn.addEventListener("click", async () => {
  currentBoardId = document.getElementById("boardId").value;
  await renderBoard();
  connectWebSocket();
});

function connectWebSocket() {
  if (stompClient && stompClient.connected) {
    stompClient.disconnect();
  }
  const socket = new SockJS(`${API_BASE}/ws`);
  stompClient = Stomp.over(socket);
  stompClient.debug = null;
  connectBtn.disabled = true;

  stompClient.connect(
    { Authorization: "Bearer " + token },
    () => {
      statusEl.textContent = "connected";
      statusEl.className = "connected";
      stompClient.subscribe(`/topic/board/${currentBoardId}`, (message) => {
        const event = JSON.parse(message.body);
        addFeedEntry(event);
        renderBoard();
      });
    },
    () => {
      statusEl.textContent = "connection failed";
      statusEl.className = "disconnected";
      connectBtn.disabled = false;
    }
  );
}

let isRendering = false;
let renderQueued = false;

async function renderBoard() {
  if (isRendering) {
    renderQueued = true;
    return;
  }
  isRendering = true;

  const columns = await (await fetch(`${API_BASE}/boards/${currentBoardId}/columns`, { headers: authHeaders() })).json();
  boardEl.innerHTML = "";

  for (const column of columns) {
    const cards = await (await fetch(`${API_BASE}/columns/${column.id}/cards`, { headers: authHeaders() })).json();

    const colDiv = document.createElement("div");
    colDiv.className = "column";
    colDiv.dataset.columnId = column.id;

    const h3 = document.createElement("h3");
    h3.textContent = column.title;
    const countSpan = document.createElement("span");
    countSpan.className = "count";
    countSpan.textContent = cards.length;
    h3.appendChild(countSpan);
    colDiv.appendChild(h3);

    const cardsWrap = document.createElement("div");
    cardsWrap.className = "cards-wrap";

    cards.forEach(card => {
      const cardDiv = document.createElement("div");
      cardDiv.className = "card";
      cardDiv.draggable = true;
      cardDiv.dataset.cardId = card.id;
      cardDiv.textContent = card.title;
      if (card.assigneeName) {
      const assigneeDiv = document.createElement("div");
      assigneeDiv.className = "assignee";
      assigneeDiv.textContent = `👤 ${card.assigneeName}`;
      cardDiv.appendChild(assigneeDiv);
      }

      cardDiv.addEventListener("dragstart", (e) => {
        draggedCardId = card.id;
        e.dataTransfer.setData("text/plain", card.id);
        e.dataTransfer.effectAllowed = "move";
        cardDiv.classList.add("dragging");
      });
      cardDiv.addEventListener("dragend", () => cardDiv.classList.remove("dragging"));

      cardsWrap.appendChild(cardDiv);
    });

    colDiv.appendChild(cardsWrap);

    const form = document.createElement("div");
    form.className = "add-card-form";
    form.innerHTML = `<input placeholder="New card title"><button class="ghost">Add</button>`;
    const input = form.querySelector("input");
    const addBtn = form.querySelector("button");
    addBtn.addEventListener("click", async () => {
      if (!input.value.trim() || addBtn.disabled) return;
      addBtn.disabled = true;
      addBtn.textContent = "...";
      try {
        await fetch(`${API_BASE}/columns/${column.id}/cards`, {
          method: "POST",
          headers: authHeaders({ "Content-Type": "application/json" }),
          body: JSON.stringify({ title: input.value })
        });
        input.value = "";
        await renderBoard();
      } finally {
        addBtn.disabled = false;
        addBtn.textContent = "Add";
      }
    });
    colDiv.appendChild(form);

    colDiv.addEventListener("dragover", (e) => { e.preventDefault(); colDiv.classList.add("drag-over"); });
    colDiv.addEventListener("dragleave", () => colDiv.classList.remove("drag-over"));
    colDiv.addEventListener("drop", async (e) => {
      e.preventDefault();
      colDiv.classList.remove("drag-over");
      if (!draggedCardId) return;
      const movingId = draggedCardId;
      draggedCardId = null;
      await fetch(`${API_BASE}/cards/${movingId}/move`, {
        method: "PATCH",
        headers: authHeaders({ "Content-Type": "application/json" }),
        body: JSON.stringify({ targetColumnId: column.id, newPosition: cards.length })
      });
      await renderBoard();
    });

    boardEl.appendChild(colDiv);
  }

  isRendering = false;
  if (renderQueued) {
    renderQueued = false;
    renderBoard();
  }
}

function addFeedEntry(event) {
  const div = document.createElement("div");
  div.className = "entry";

  const strong = document.createElement("strong");
  strong.textContent = event.userName;

  const metaDiv = document.createElement("div");
  metaDiv.className = "meta";
  metaDiv.textContent = new Date(event.timestamp).toLocaleTimeString();

  div.appendChild(strong);
  div.append(` — ${event.action}: ${event.details}`);
  div.appendChild(metaDiv);

  feedEl.prepend(div);
}