import { apiRequest } from "./app.js";

const nameEl = document.getElementById("tag-name");
const colorEl = document.getElementById("tag-colour");
const listEl = document.getElementById("tags");
const createBtn = document.getElementById("create-tag");

async function loadTags() {
  const tags = await apiRequest("/tag/all");
  listEl.innerHTML = "";
  tags.forEach(t => {
    const li = document.createElement("li");
    li.innerHTML = `
      <span style="color:${t.colour}">#</span> ${t.name}
      <button onclick="deleteTag(${t.id})">Удалить</button>
    `;
    listEl.appendChild(li);
  });
}

window.deleteTag = async function(id) {
  await apiRequest(`/tag/${id}`, "DELETE");
  loadTags();
};

createBtn.addEventListener("click", async () => {
  await apiRequest("/tag", "POST", { name: nameEl.value, colour: colorEl.value });
  nameEl.value = "";
  loadTags();
});

loadTags();
