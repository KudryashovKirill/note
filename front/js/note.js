document.getElementById('create-note').addEventListener('click', async () => {
    const name = document.getElementById('note-name').value;
    const categoriesInput = document.getElementById('note-categories').value;
    const tagsInput = document.getElementById('note-tags').value;

    // Преобразуем строки в массив объектов DTO
    const categories = categoriesInput.split(',')
        .map(c => c.trim())
        .filter(c => c.length > 0)
        .map(name => ({ name }));

    const tags = tagsInput.split(',')
        .map(t => t.trim())
        .filter(t => t.length > 0)
        .map(name => ({ name, colour: '#000000' })); // дефолтный цвет

    const noteDto = {
        name,
        dateOfCreation: new Date().toISOString().split('T')[0],
        dateOfUpdate: new Date().toISOString().split('T')[0],
        isDone: false,
        categories,
        tags
    };

    try {
        const response = await fetch('http://localhost:8080/notes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(noteDto)
        });

        if (!response.ok) throw new Error('Ошибка при создании заметки');

        const savedNote = await response.json();
        alert(`Заметка "${savedNote.name}" создана!`);
        // Тут можно обновить список заметок на странице
    } catch (error) {
        console.error(error);
        alert('Не удалось создать заметку');
    }
});
