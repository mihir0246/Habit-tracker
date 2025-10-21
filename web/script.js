// Global variables
let habits = [];
let currentDate = new Date();
let selectedDate = new Date();

// Initialize the app
document.addEventListener('DOMContentLoaded', function() {
    loadHabits();
    renderCalendar();
    renderHabits();
    updateStats();

    // Event listeners
    document.getElementById('prevMonth').addEventListener('click', () => {
        currentDate.setMonth(currentDate.getMonth() - 1);
        renderCalendar();
    });

    document.getElementById('nextMonth').addEventListener('click', () => {
        currentDate.setMonth(currentDate.getMonth() + 1);
        renderCalendar();
    });

    document.getElementById('addHabitBtn').addEventListener('click', openAddHabitModal);
    document.getElementById('addHabitForm').addEventListener('submit', addHabit);
    document.getElementById('saveBtn').addEventListener('click', saveHabits);

    // Modal close buttons
    document.querySelectorAll('.close').forEach(btn => {
        btn.addEventListener('click', function() {
            this.closest('.modal').style.display = 'none';
        });
    });

    // Close modal when clicking outside
    window.addEventListener('click', function(event) {
        if (event.target.classList.contains('modal')) {
            event.target.style.display = 'none';
        }
    });
});

// Load habits from localStorage
function loadHabits() {
    const savedHabits = localStorage.getItem('habits');
    if (savedHabits) {
        habits = JSON.parse(savedHabits);
    }
}

// Save habits to localStorage
function saveHabits() {
    localStorage.setItem('habits', JSON.stringify(habits));
    showNotification('Habits saved successfully!');
}

// Render calendar
function renderCalendar() {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();

    // Update month header
    document.getElementById('currentMonth').textContent = 
        new Date(year, month).toLocaleDateString('en-US', { month: 'long', year: 'numeric' });

    // Clear calendar
    const calendar = document.getElementById('calendar');
    calendar.innerHTML = '';

    // Add day headers
    const dayHeaders = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
    dayHeaders.forEach(day => {
        const dayHeader = document.createElement('div');
        dayHeader.className = 'day-header';
        dayHeader.textContent = day;
        calendar.appendChild(dayHeader);
    });

    // Get first day of month and number of days
    const firstDay = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    // Add empty cells for days before month starts
    for (let i = 0; i < firstDay; i++) {
        const emptyDay = document.createElement('div');
        calendar.appendChild(emptyDay);
    }

    // Add days of the month
    for (let day = 1; day <= daysInMonth; day++) {
        const dayElement = document.createElement('div');
        dayElement.className = 'day';
        dayElement.textContent = day;

        const date = new Date(year, month, day);
        const dateStr = formatDateKey(date);

        // Check if this is today
        const today = new Date();
        if (date.toDateString() === today.toDateString()) {
            dayElement.classList.add('today');
        }

        // Check if this is the selected date
        if (date.toDateString() === selectedDate.toDateString()) {
            dayElement.classList.add('selected');
        }

        // Check if this date has habits
        if (hasHabitsForDate(dateStr)) {
            dayElement.classList.add('has-habits');
        }

        // Add click event
        dayElement.addEventListener('click', () => {
            selectedDate = date;
            renderCalendar();
            showHabitDetailsForDate(dateStr);
        });

        calendar.appendChild(dayElement);
    }
}

// Check if a date has habits
function hasHabitsForDate(dateStr) {
    return habits.some(habit => {
        return habit.completions && habit.completions[dateStr];
    });
}

// Show habit details for a specific date
function showHabitDetailsForDate(dateStr) {
    const modal = document.getElementById('habitDetailsModal');
    const title = document.getElementById('habitDetailsTitle');
    const content = document.getElementById('habitDetailsContent');

    const date = new Date(dateStr);
    title.textContent = `Habit Status for ${date.toLocaleDateString()}`;

    // Clear previous content
    content.innerHTML = '';

    // Check each habit
    let hasCompletedHabits = false;
    habits.forEach(habit => {
        const isCompleted = habit.completions && habit.completions[dateStr];
        if (isCompleted) {
            hasCompletedHabits = true;

            const habitDiv = document.createElement('div');
            habitDiv.className = 'habit-item';
            habitDiv.innerHTML = `
                <div class="habit-info">
                    <h3>${habit.name}</h3>
                    <div class="habit-category">${habit.category}</div>
                </div>
                <div class="habit-streak">
                    <i class="fas fa-fire"></i> ${habit.streak} day streak
                </div>
            `;
            content.appendChild(habitDiv);
        }
    });

    if (!hasCompletedHabits) {
        content.innerHTML = '<p>No habits were completed on this day.</p>';
    }

    modal.style.display = 'block';
}

// Render habits list
function renderHabits() {
    const habitsList = document.getElementById('habitsList');
    habitsList.innerHTML = '';

    habits.forEach((habit, index) => {
        const habitDiv = document.createElement('div');
        habitDiv.className = 'habit-item';

        const dateStr = formatDateKey(new Date());
        const isCompleted = habit.completions && habit.completions[dateStr];

        habitDiv.innerHTML = `
            <div class="habit-info">
                <h3>${habit.name}</h3>
                <div class="habit-category">${habit.category}</div>
            </div>
            <div class="habit-streak">
                <i class="fas fa-fire"></i> ${habit.streak} day streak
            </div>
            <div class="habit-actions">
                <input type="checkbox" class="habit-checkbox" ${isCompleted ? 'checked' : ''} data-index="${index}">
                <button class="habit-delete" data-index="${index}"><i class="fas fa-trash"></i></button>
            </div>
        `;

        habitsList.appendChild(habitDiv);
    });

    // Add event listeners to checkboxes
    document.querySelectorAll('.habit-checkbox').forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const index = parseInt(this.getAttribute('data-index'));
            toggleHabitCompletion(index);
        });
    });

    // Add event listeners to delete buttons
    document.querySelectorAll('.habit-delete').forEach(button => {
        button.addEventListener('click', function() {
            const index = parseInt(this.getAttribute('data-index'));
            deleteHabit(index);
        });
    });
}

// Toggle habit completion
function toggleHabitCompletion(index) {
    const habit = habits[index];
    const dateStr = formatDateKey(new Date());

    if (!habit.completions) {
        habit.completions = {};
    }

    if (habit.completions[dateStr]) {
        // Already completed today, uncheck it
        delete habit.completions[dateStr];
        habit.streak = calculateStreak(habit);
    } else {
        // Mark as completed today
        habit.completions[dateStr] = true;
        habit.streak = calculateStreak(habit);
    }

    renderHabits();
    renderCalendar();
    updateStats();
}

// Calculate streak for a habit
function calculateStreak(habit) {
    if (!habit.completions || Object.keys(habit.completions).length === 0) {
        return 0;
    }

    // Get all dates when habit was completed
    const completedDates = Object.keys(habit.completions)
        .map(dateStr => new Date(dateStr))
        .sort((a, b) => b - a); // Sort in descending order

    let streak = 0;
    let currentDate = new Date();
    currentDate.setHours(0, 0, 0, 0); // Set to start of day

    for (let i = 0; i < completedDates.length; i++) {
        const completedDate = new Date(completedDates[i]);
        completedDate.setHours(0, 0, 0, 0);

        // If the completed date is today or yesterday
        const daysDiff = Math.floor((currentDate - completedDate) / (1000 * 60 * 60 * 24));

        if (daysDiff === streak) {
            streak++;
        } else {
            break;
        }
    }

    return streak;
}

// Delete a habit
function deleteHabit(index) {
    if (confirm('Are you sure you want to delete this habit?')) {
        habits.splice(index, 1);
        renderHabits();
        renderCalendar();
        updateStats();
    }
}

// Open add habit modal
function openAddHabitModal() {
    document.getElementById('addHabitModal').style.display = 'block';
    document.getElementById('habitName').value = '';
    document.getElementById('habitCategory').value = '';
}

// Add a new habit
function addHabit(event) {
    event.preventDefault();

    const name = document.getElementById('habitName').value.trim();
    const category = document.getElementById('habitCategory').value.trim();

    if (name && category) {
        const newHabit = {
            name: name,
            category: category,
            streak: 0,
            completions: {}
        };

        habits.push(newHabit);
        renderHabits();
        updateStats();

        document.getElementById('addHabitModal').style.display = 'none';
    }
}

// Update statistics
function updateStats() {
    const totalHabits = habits.length;
    const dateStr = formatDateKey(new Date());
    const completedToday = habits.filter(habit => 
        habit.completions && habit.completions[dateStr]
    ).length;

    document.getElementById('totalHabits').textContent = totalHabits;
    document.getElementById('completedToday').textContent = completedToday;
}

// Format date as YYYY-MM-DD
function formatDateKey(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// Show notification
function showNotification(message) {
    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.textContent = message;
    notification.style.position = 'fixed';
    notification.style.bottom = '20px';
    notification.style.right = '20px';
    notification.style.backgroundColor = '#4CAF50';
    notification.style.color = 'white';
    notification.style.padding = '15px';
    notification.style.borderRadius = '5px';
    notification.style.boxShadow = '0 4px 8px rgba(0,0,0,0.1)';
    notification.style.zIndex = '1000';
    notification.style.animation = 'slideIn 0.3s, fadeOut 0.5s 2.5s forwards';

    document.body.appendChild(notification);

    // Remove notification after animation
    setTimeout(() => {
        document.body.removeChild(notification);
    }, 3000);
}
