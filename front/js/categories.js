import { apiRequest } from "./app.js";

const nameEl = document.getElementById("category-name");
const listEl = document.getElementById("categories");
const createBtn = document.getElementById("create-category");

async function loadCategories() {
  const cats = await apiRequest("/category/all");
  listEl.innerHTML = "";
  cats.forEach(c => {
    const li = document.createElement("li");
    li.innerHTML = `${c.name} <button onclick="deleteCategory(${c.id})">Удалить</button>`;
    listEl.appendChild(li);
  });
}

window.deleteCategory = async function(id) {
  await apiRequest(`/category/${id}`, "DELETE");
  loadCategories();
};

createBtn.addEventListener("click", async () => {
  await apiRequest("/category", "POST", { name: nameEl.value });
  nameEl.value = "";
  loadCategories();
});

loadCategories();
