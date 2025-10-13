const API_BASE = "http://localhost:8080";

// --- Создание заметки ---
document.getElementById("create-note-btn").addEventListener("click", async () => {
  const title = document.getElementById("note-title").value;
  const content = document.getElementById("note-content").value;
  const categories = document.getElementById("note-categories").value.split(",").map(c => c.trim()).filter(Boolean);
  const tags = document.getElementById("note-tags").value.split(",").map(t => t.trim()).filter(Boolean);

  const noteDto = {
    title: title,
    content: content,
    categories: categories.map(name => ({ name })),
    tags: tags.map(name => ({ name }))
  };

  const res = await fetch(`${API_BASE}/notes`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(noteDto)
  });

  if (res.ok) {
    alert("Заметка создана!");
    loadNotes();
  } else {
    alert("Ошибка при создании заметки");
  }
});

// --- Получение всех заметок ---
async function loadNotes() {
  const res = await fetch(`${API_BASE}/notes/all`);
  if (res.ok) {
    const notes = await res.json();
    renderNotes(notes);
  } else {
    console.error("Ошибка загрузки заметок");
  }
}

// --- Отображение заметок ---
function renderNotes(notes) {
  const list = document.getElementById("notes");
  list.innerHTML = "";

  notes.forEach(note => {
    const li = document.createElement("li");
    li.innerHTML = `
      <h3>${note.title}</h3>
      <p>${note.content}</p>
      <small>Категории: ${(note.categories || []).map(c => c.name).join(", ")}</small><br>
      <small>Теги: ${(note.tags || []).map(t => t.name).join(", ")}</small><br>
      <button onclick="deleteNote(${note.id})">Удалить</button>
    `;
    list.appendChild(li);
  });
}

// --- Удаление заметки ---
async function deleteNote(id) {
  const res = await fetch(`${API_BASE}/notes/${id}`, { method: "DELETE" });
  if (res.ok) {
    alert("Заметка удалена");
    loadNotes();
  } else {
    alert("Ошибка при удалении");
  }
}

// При загрузке страницы
loadNotes();
