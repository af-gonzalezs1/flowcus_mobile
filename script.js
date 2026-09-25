const signupForm = document.querySelector('#signup-form');
if (signupForm) {
  const password = document.querySelector('#password');
  const toggle = document.querySelector('.toggle-password');
  const message = document.querySelector('#form-message');

  toggle.addEventListener('click', () => {
    const showing = password.type === 'password';
    password.type = showing ? 'text' : 'password';
    toggle.setAttribute('aria-pressed', String(showing));
    toggle.setAttribute('aria-label', showing ? 'Ocultar contraseña' : 'Mostrar contraseña');
  });

  signupForm.addEventListener('submit', (event) => {
    event.preventDefault();
    message.textContent = '';
    if (!signupForm.reportValidity()) return;
    if (!/(?=.*\d)(?=.*[^A-Za-z0-9])/.test(password.value)) {
      message.textContent = 'La contraseña debe incluir al menos un número y un símbolo.';
      password.focus();
      return;
    }
    window.location.href = 'tareas.html';
  });

  signupForm.addEventListener('input', () => {
    message.textContent = '';
    message.style.color = '';
  });
}

function escapeHTML(value) {
  return String(value).replace(/[&<>"']/g, (character) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;',
  })[character]);
}

const mobileTaskList = document.querySelector('#mobile-task-list');
if (mobileTaskList) {
  let savedTasks = [];
  try { savedTasks = JSON.parse(localStorage.getItem('flowcus-tasks') || '[]'); }
  catch { savedTasks = []; }

  savedTasks.forEach((task, index) => {
    const card = document.createElement('article');
    card.className = 'mobile-task-card';
    card.dataset.title = task.title;
    card.dataset.status = 'ready';
    const priorityClass = task.priority === 'Alta' ? 'high' : task.priority === 'Media' ? 'medium' : 'low';
    card.innerHTML = `<input type="checkbox" aria-label="Completar ${escapeHTML(task.title)}"><a href="crear-tarea.html?task=${index + 5}"><strong>${escapeHTML(task.title)}</strong><span>Estimado:</span><small>${escapeHTML(task.cycles)} CICLO${Number(task.cycles) === 1 ? '' : 'S'} <b class="${priorityClass}">${escapeHTML(task.priority.toUpperCase())}</b></small></a>`;
    mobileTaskList.append(card);
  });

  const search = document.querySelector('#mobile-task-search');
  const tabs = [...document.querySelectorAll('[data-mobile-filter]')];
  const cards = [...mobileTaskList.querySelectorAll('.mobile-task-card')];
  let selectedFilter = 'all';

  const refreshMobileTasks = () => {
    const query = search.value.trim().toLocaleLowerCase('es');
    cards.forEach((card) => {
      card.hidden = (selectedFilter !== 'all' && card.dataset.status !== selectedFilter)
        || !card.dataset.title.toLocaleLowerCase('es').includes(query);
    });
    const counts = {
      all: cards.length,
      active: cards.filter((card) => card.dataset.status === 'active').length,
      done: cards.filter((card) => card.dataset.status === 'done').length,
    };
    tabs.forEach((tab) => { tab.querySelector('span').textContent = `(${counts[tab.dataset.mobileFilter]})`; });
  };

  tabs.forEach((tab) => tab.addEventListener('click', () => {
    selectedFilter = tab.dataset.mobileFilter;
    tabs.forEach((item) => item.classList.toggle('active', item === tab));
    refreshMobileTasks();
  }));
  search.addEventListener('input', refreshMobileTasks);
  cards.forEach((card) => card.querySelector('input').addEventListener('change', (event) => {
    card.dataset.status = event.target.checked ? 'done' : 'ready';
    card.classList.toggle('is-done', event.target.checked);
    refreshMobileTasks();
  }));
  document.querySelector('#mobile-history').addEventListener('click', () => {
    tabs.find((tab) => tab.dataset.mobileFilter === 'done').click();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  });
  refreshMobileTasks();
}

const mobileDetail = document.querySelector('.mobile-detail');
if (mobileDetail) {
  const taskNumber = Number(new URLSearchParams(window.location.search).get('task') || 1);
  let savedTasks = [];
  try { savedTasks = JSON.parse(localStorage.getItem('flowcus-tasks') || '[]'); }
  catch { savedTasks = []; }
  const savedIndex = taskNumber - 5;
  const savedTask = savedIndex >= 0 ? savedTasks[savedIndex] : null;
  const title = document.querySelector('#mobile-detail-title');
  const description = document.querySelector('#mobile-detail-description');
  const priority = document.querySelector('#mobile-detail-priority');
  const category = document.querySelector('#mobile-detail-category');
  const estimate = document.querySelector('#mobile-detail-estimate');
  const minutes = document.querySelector('#mobile-detail-minutes');

  if (savedTask) {
    title.textContent = savedTask.title;
    description.textContent = savedTask.description || 'Sin descripción por ahora.';
    priority.textContent = savedTask.priority.toUpperCase();
    priority.classList.toggle('medium', savedTask.priority === 'Media');
    priority.classList.toggle('low', savedTask.priority === 'Baja');
    category.textContent = savedTask.category.toUpperCase();
    const cycles = Number(savedTask.cycles) || 1;
    estimate.textContent = `${cycles} CICLO${cycles === 1 ? '' : 'S'} (${cycles * 25} MIN ENFOQUE)`;
    minutes.textContent = `${cycles}×25M`;
  } else if (taskNumber >= 2 && taskNumber <= 4) {
    title.textContent = `TAREA ${taskNumber}`;
    description.textContent = `Detalles de la tarea ${taskNumber}. Revisa sus subtareas antes de iniciar el bloque de concentración.`;
    if (taskNumber === 2 || taskNumber === 4) {
      priority.textContent = 'MEDIA';
      priority.classList.add('medium');
    }
    if (taskNumber === 3 || taskNumber === 4) {
      estimate.textContent = '1 CICLO (25 MIN ENFOQUE)';
      minutes.textContent = '1×25M';
    }
  }

  const dialog = document.querySelector('#mobile-edit-dialog');
  document.querySelector('#mobile-edit-task').addEventListener('click', () => {
    document.querySelector('#mobile-edit-title').value = title.textContent;
    document.querySelector('#mobile-edit-description').value = description.textContent;
    dialog.showModal();
  });
  document.querySelector('#mobile-edit-cancel').addEventListener('click', () => dialog.close());
  document.querySelector('#mobile-edit-form').addEventListener('submit', (event) => {
    event.preventDefault();
    title.textContent = document.querySelector('#mobile-edit-title').value.trim() || title.textContent;
    description.textContent = document.querySelector('#mobile-edit-description').value.trim() || 'Sin descripción por ahora.';
    if (savedTask) {
      savedTask.title = title.textContent;
      savedTask.description = description.textContent;
      savedTasks[savedIndex] = savedTask;
      try { localStorage.setItem('flowcus-tasks', JSON.stringify(savedTasks)); } catch { /* Estado local opcional. */ }
    }
    dialog.close();
  });
  document.querySelector('#mobile-use-task').addEventListener('click', (event) => {
    try { sessionStorage.setItem('flowcus-active-task', title.textContent); } catch { /* Estado local opcional. */ }
    event.currentTarget.textContent = '✓ EN FLOWCUS';
  });
}
