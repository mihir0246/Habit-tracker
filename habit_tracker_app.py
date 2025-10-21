
import tkinter as tk
from tkinter import ttk, messagebox, simpledialog
import json
import os
from datetime import datetime, date, timedelta
import calendar

class HabitTrackerApp:
    def __init__(self, root):
        self.root = root
        self.root.title("Habit Tracker")
        self.root.geometry("1200x800")
        self.root.configure(bg="#f5f7fa")

        # Initialize variables
        self.habits = []
        self.current_date = date.today()
        self.selected_date = date.today()

        # Load habits from file
        self.load_habits()

        # Create UI elements
        self.create_ui()

        # Update UI with data
        self.render_calendar()
        self.render_habits()
        self.update_stats()

    def create_ui(self):
        # Main container
        main_container = tk.Frame(self.root, bg="#f5f7fa")
        main_container.pack(fill=tk.BOTH, expand=True, padx=20, pady=20)

        # Header
        self.create_header(main_container)

        # Main content area
        content_area = tk.Frame(main_container, bg="#f5f7fa")
        content_area.pack(fill=tk.BOTH, expand=True, pady=20)

        # Calendar and habits container
        self.create_calendar_container(content_area)
        self.create_habits_container(content_area)

        # Footer
        self.create_footer(main_container)

    def create_header(self, parent):
        header_frame = tk.Frame(parent, bg="#6a82fb", height=120)
        header_frame.pack(fill=tk.X, pady=(0, 30))
        header_frame.pack_propagate(False)

        # Title
        title_label = tk.Label(
            header_frame, 
            text="My Habit Tracker", 
            font=("Segoe UI", 24, "bold"),
            bg="#6a82fb",
            fg="white"
        )
        title_label.pack(pady=20)

        # Stats container
        stats_frame = tk.Frame(header_frame, bg="#6a82fb")
        stats_frame.pack()

        # Total habits stat
        total_habits_frame = tk.Frame(stats_frame, bg="#6a82fb")
        total_habits_frame.pack(side=tk.LEFT, padx=40)

        self.total_habits_label = tk.Label(
            total_habits_frame,
            text="0",
            font=("Segoe UI", 20, "bold"),
            bg="#6a82fb",
            fg="white"
        )
        self.total_habits_label.pack()

        tk.Label(
            total_habits_frame,
            text="Total Habits",
            font=("Segoe UI", 12),
            bg="#6a82fb",
            fg="white"
        ).pack()

        # Completed today stat
        completed_today_frame = tk.Frame(stats_frame, bg="#6a82fb")
        completed_today_frame.pack(side=tk.LEFT, padx=40)

        self.completed_today_label = tk.Label(
            completed_today_frame,
            text="0",
            font=("Segoe UI", 20, "bold"),
            bg="#6a82fb",
            fg="white"
        )
        self.completed_today_label.pack()

        tk.Label(
            completed_today_frame,
            text="Completed Today",
            font=("Segoe UI", 12),
            bg="#6a82fb",
            fg="white"
        ).pack()

    def create_calendar_container(self, parent):
        calendar_container = tk.Frame(parent, bg="white", relief=tk.RAISED, bd=1)
        calendar_container.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=(0, 15))

        # Calendar header
        calendar_header = tk.Frame(calendar_container, bg="white")
        calendar_header.pack(fill=tk.X, padx=20, pady=20)

        # Previous month button
        prev_month_btn = tk.Button(
            calendar_header,
            text="<",
            bg="white",
            fg="#6a82fb",
            bd=0,
            font=("Segoe UI", 12),
            command=self.prev_month
        )
        prev_month_btn.pack(side=tk.LEFT, padx=10)

        # Current month label
        self.current_month_label = tk.Label(
            calendar_header,
            text="",
            font=("Segoe UI", 16),
            bg="white",
            fg="#333"
        )
        self.current_month_label.pack(side=tk.LEFT, expand=True)

        # Next month button
        next_month_btn = tk.Button(
            calendar_header,
            text=">",
            bg="white",
            fg="#6a82fb",
            bd=0,
            font=("Segoe UI", 12),
            command=self.next_month
        )
        next_month_btn.pack(side=tk.RIGHT, padx=10)

        # Calendar grid
        self.calendar_frame = tk.Frame(calendar_container, bg="white")
        self.calendar_frame.pack(fill=tk.BOTH, expand=True, padx=20, pady=(0, 20))

    def create_habits_container(self, parent):
        habits_container = tk.Frame(parent, bg="white", relief=tk.RAISED, bd=1)
        habits_container.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True)

        # Habits header
        habits_header = tk.Frame(habits_container, bg="white")
        habits_header.pack(fill=tk.X, padx=20, pady=20)

        tk.Label(
            habits_header,
            text="Your Habits",
            font=("Segoe UI", 16),
            bg="white",
            fg="#333"
        ).pack(side=tk.LEFT)

        # Add habit button
        add_habit_btn = tk.Button(
            habits_header,
            text="+ Add Habit",
            bg="#6a82fb",
            fg="white",
            bd=0,
            font=("Segoe UI", 10, "bold"),
            padx=10,
            pady=5,
            command=self.open_add_habit_dialog
        )
        add_habit_btn.pack(side=tk.RIGHT)

        # Habits list
        self.habits_frame = tk.Frame(habits_container, bg="white")
        self.habits_frame.pack(fill=tk.BOTH, expand=True, padx=20, pady=(0, 20))

        # Add scrollable frame for habits
        habits_scroll_frame = tk.Frame(self.habits_frame, bg="white")
        habits_scroll_frame.pack(fill=tk.BOTH, expand=True)

        # Create canvas and scrollbar
        habits_canvas = tk.Canvas(habits_scroll_frame, bg="white", highlightthickness=0)
        scrollbar = ttk.Scrollbar(habits_scroll_frame, orient="vertical", command=habits_canvas.yview)
        self.scrollable_habits_frame = tk.Frame(habits_canvas, bg="white")

        self.scrollable_habits_frame.bind(
            "<Configure>",
            lambda e: habits_canvas.configure(scrollregion=habits_canvas.bbox("all"))
        )

        habits_canvas.create_window((0, 0), window=self.scrollable_habits_frame, anchor="nw")
        habits_canvas.configure(yscrollcommand=scrollbar.set)

        habits_canvas.pack(side="left", fill="both", expand=True)
        scrollbar.pack(side="right", fill="y")

    def create_footer(self, parent):
        footer_frame = tk.Frame(parent, bg="#f5f7fa")
        footer_frame.pack(fill=tk.X, pady=(20, 0))

        save_btn = tk.Button(
            footer_frame,
            text="Save",
            bg="#6a82fb",
            fg="white",
            bd=0,
            font=("Segoe UI", 10, "bold"),
            padx=15,
            pady=10,
            command=self.save_habits
        )
        save_btn.pack()

    def render_calendar(self):
        # Clear existing calendar
        for widget in self.calendar_frame.winfo_children():
            widget.destroy()

        # Update month header
        month_name = self.current_date.strftime("%B %Y")
        self.current_month_label.config(text=month_name)

        # Add day headers
        day_headers = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"]
        for day in day_headers:
            day_label = tk.Label(
                self.calendar_frame,
                text=day,
                font=("Segoe UI", 10, "bold"),
                bg="white",
                fg="#6a82fb",
                width=5,
                height=2
            )
            day_label.grid(row=0, column=day_headers.index(day), padx=2, pady=2)

        # Get first day of month and number of days
        first_day = calendar.monthrange(self.current_date.year, self.current_date.month)[0]
        days_in_month = calendar.monthrange(self.current_date.year, self.current_date.month)[1]

        # Add empty cells for days before month starts
        row = 1
        for i in range(first_day):
            empty_label = tk.Label(
                self.calendar_frame,
                text="",
                bg="white",
                width=5,
                height=2
            )
            empty_label.grid(row=row, column=i, padx=2, pady=2)

        # Add days of the month
        for day in range(1, days_in_month + 1):
            # Calculate grid position
            if (first_day + day - 1) % 7 == 0 and day != 1:
                row += 1

            col = (first_day + day - 1) % 7

            # Create day label
            day_label = tk.Label(
                self.calendar_frame,
                text=str(day),
                font=("Segoe UI", 10),
                bg="white",
                width=5,
                height=2,
                relief=tk.RAISED,
                bd=1
            )

            # Check if this is today
            today = date.today()
            current_day = date(self.current_date.year, self.current_date.month, day)

            if current_day == today:
                day_label.config(bg="#e6f0ff", font=("Segoe UI", 10, "bold"))

            # Check if this is the selected date
            if current_day == self.selected_date:
                day_label.config(bg="#6a82fb", fg="white")

            # Check if this date has habits
            date_str = current_day.strftime("%Y-%m-%d")
            if self.has_habits_for_date(date_str):
                # Add a small indicator
                day_label.config(relief=tk.RAISED, bd=2)

            # Add click event
            day_label.bind("<Button-1>", lambda e, d=current_day: self.select_date(d))

            day_label.grid(row=row, column=col, padx=2, pady=2)

    def has_habits_for_date(self, date_str):
        return any(
            habit.get("completions", {}).get(date_str, False)
            for habit in self.habits
        )

    def select_date(self, selected_day):
        self.selected_date = selected_day
        self.render_calendar()
        self.show_habit_details_for_date(selected_day)

    def show_habit_details_for_date(self, selected_date):
        date_str = selected_date.strftime("%Y-%m-%d")
        date_display = selected_date.strftime("%B %d, %Y")

        # Create a new window to show habit details
        details_window = tk.Toplevel(self.root)
        details_window.title(f"Habit Status for {date_display}")
        details_window.geometry("500x400")
        details_window.configure(bg="white")

        # Title
        title_label = tk.Label(
            details_window,
            text=f"Habit Status for {date_display}",
            font=("Segoe UI", 16, "bold"),
            bg="white",
            fg="#333"
        )
        title_label.pack(pady=20)

        # Content frame
        content_frame = tk.Frame(details_window, bg="white")
        content_frame.pack(fill=tk.BOTH, expand=True, padx=20, pady=(0, 20))

        # Check each habit
        has_completed_habits = False
        for habit in self.habits:
            is_completed = habit.get("completions", {}).get(date_str, False)
            if is_completed:
                has_completed_habits = True

                # Create habit item
                habit_frame = tk.Frame(content_frame, bg="#f9f9f9", relief=tk.RAISED, bd=1)
                habit_frame.pack(fill=tk.X, pady=10)

                # Habit info
                habit_info = tk.Frame(habit_frame, bg="#f9f9f9")
                habit_info.pack(side=tk.LEFT, padx=15, pady=15)

                tk.Label(
                    habit_info,
                    text=habit["name"],
                    font=("Segoe UI", 12, "bold"),
                    bg="#f9f9f9",
                    fg="#333"
                ).pack(anchor=tk.W)

                tk.Label(
                    habit_info,
                    text=habit["category"],
                    font=("Segoe UI", 10),
                    bg="#f9f9f9",
                    fg="#666"
                ).pack(anchor=tk.W)

                # Habit streak
                streak_frame = tk.Frame(habit_frame, bg="#f9f9f9")
                streak_frame.pack(side=tk.RIGHT, padx=15, pady=15)

                tk.Label(
                    streak_frame,
                    text=f"🔥 {habit.get('streak', 0)} day streak",
                    font=("Segoe UI", 10, "bold"),
                    bg="#f9f9f9",
                    fg="#fc5c7d"
                ).pack()

        if not has_completed_habits:
            tk.Label(
                content_frame,
                text="No habits were completed on this day.",
                font=("Segoe UI", 12),
                bg="white",
                fg="#666"
            ).pack(pady=20)

    def render_habits(self):
        # Clear existing habits
        for widget in self.scrollable_habits_frame.winfo_children():
            widget.destroy()

        # Add each habit
        for index, habit in enumerate(self.habits):
            # Create habit frame
            habit_frame = tk.Frame(self.scrollable_habits_frame, bg="#f9f9f9", relief=tk.RAISED, bd=1)
            habit_frame.pack(fill=tk.X, pady=10)

            # Habit info
            habit_info = tk.Frame(habit_frame, bg="#f9f9f9")
            habit_info.pack(side=tk.LEFT, padx=15, pady=15)

            tk.Label(
                habit_info,
                text=habit["name"],
                font=("Segoe UI", 12, "bold"),
                bg="#f9f9f9",
                fg="#333"
            ).pack(anchor=tk.W)

            tk.Label(
                habit_info,
                text=habit["category"],
                font=("Segoe UI", 10),
                bg="#f9f9f9",
                fg="#666"
            ).pack(anchor=tk.W)

            # Habit streak
            streak_frame = tk.Frame(habit_frame, bg="#f9f9f9")
            streak_frame.pack(side=tk.RIGHT, padx=15, pady=15)

            tk.Label(
                streak_frame,
                text=f"🔥 {habit.get('streak', 0)} day streak",
                font=("Segoe UI", 10, "bold"),
                bg="#f9f9f9",
                fg="#fc5c7d"
            ).pack()

            # Habit actions
            actions_frame = tk.Frame(habit_frame, bg="#f9f9f9")
            actions_frame.pack(side=tk.RIGHT, padx=15, pady=15)

            # Checkbox for completion
            date_str = date.today().strftime("%Y-%m-%d")
            is_completed = habit.get("completions", {}).get(date_str, False)

            completed_var = tk.BooleanVar(value=is_completed)
            checkbox = tk.Checkbutton(
                actions_frame,
                variable=completed_var,
                bg="#f9f9f9",
                command=lambda i=index, var=completed_var: self.toggle_habit_completion(i, var)
            )
            checkbox.pack(side=tk.LEFT, padx=5)

            # Delete button
            delete_btn = tk.Button(
                actions_frame,
                text="🗑",
                bg="#f9f9f9",
                fg="#ff4757",
                bd=0,
                font=("Segoe UI", 12),
                command=lambda i=index: self.delete_habit(i)
            )
            delete_btn.pack(side=tk.LEFT, padx=5)

    def toggle_habit_completion(self, index, var):
        habit = self.habits[index]
        date_str = date.today().strftime("%Y-%m-%d")

        if "completions" not in habit:
            habit["completions"] = {}

        if var.get():
            # Mark as completed today
            habit["completions"][date_str] = True
        else:
            # Unmark as completed today
            if date_str in habit["completions"]:
                del habit["completions"][date_str]

        # Update streak
        habit["streak"] = self.calculate_streak(habit)

        # Refresh UI
        self.render_habits()
        self.render_calendar()
        self.update_stats()

    def calculate_streak(self, habit):
        if "completions" not in habit or not habit["completions"]:
            return 0

        # Get all dates when habit was completed
        completed_dates = [
            datetime.strptime(date_str, "%Y-%m-%d").date()
            for date_str in habit["completions"].keys()
        ]
        completed_dates.sort(reverse=True)  # Sort in descending order

        streak = 0
        current_date = date.today()

        for completed_date in completed_dates:
            # Calculate days difference
            days_diff = (current_date - completed_date).days

            if days_diff == streak:
                streak += 1
            else:
                break

        return streak

    def delete_habit(self, index):
        if messagebox.askyesno("Delete Habit", "Are you sure you want to delete this habit?"):
            self.habits.pop(index)
            self.render_habits()
            self.render_calendar()
            self.update_stats()

    def open_add_habit_dialog(self):
        dialog = tk.Toplevel(self.root)
        dialog.title("Add New Habit")
        dialog.geometry("400x300")
        dialog.configure(bg="white")

        # Title
        title_label = tk.Label(
            dialog,
            text="Add New Habit",
            font=("Segoe UI", 16, "bold"),
            bg="white",
            fg="#333"
        )
        title_label.pack(pady=20)

        # Form frame
        form_frame = tk.Frame(dialog, bg="white")
        form_frame.pack(fill=tk.BOTH, expand=True, padx=20, pady=(0, 20))

        # Habit name
        tk.Label(
            form_frame,
            text="Habit Name",
            font=("Segoe UI", 10, "bold"),
            bg="white",
            fg="#333"
        ).pack(anchor=tk.W, pady=(10, 5))

        name_entry = tk.Entry(form_frame, font=("Segoe UI", 10))
        name_entry.pack(fill=tk.X, pady=(0, 10))
        name_entry.focus()

        # Habit category
        tk.Label(
            form_frame,
            text="Category",
            font=("Segoe UI", 10, "bold"),
            bg="white",
            fg="#333"
        ).pack(anchor=tk.W, pady=(10, 5))

        category_entry = tk.Entry(form_frame, font=("Segoe UI", 10))
        category_entry.pack(fill=tk.X, pady=(0, 10))

        # Add button
        add_btn = tk.Button(
            form_frame,
            text="Add Habit",
            bg="#6a82fb",
            fg="white",
            bd=0,
            font=("Segoe UI", 10, "bold"),
            padx=15,
            pady=10,
            command=lambda: self.add_habit(name_entry.get(), category_entry.get(), dialog)
        )
        add_btn.pack(pady=10)

    def add_habit(self, name, category, dialog):
        if not name or not category:
            messagebox.showerror("Error", "Please enter both habit name and category.")
            return

        new_habit = {
            "name": name,
            "category": category,
            "streak": 0,
            "completions": {}
        }

        self.habits.append(new_habit)
        self.render_habits()
        self.update_stats()

        dialog.destroy()

    def update_stats(self):
        # Update total habits
        self.total_habits_label.config(text=str(len(self.habits)))

        # Update completed today
        date_str = date.today().strftime("%Y-%m-%d")
        completed_today = sum(
            1 for habit in self.habits
            if habit.get("completions", {}).get(date_str, False)
        )
        self.completed_today_label.config(text=str(completed_today))

    def prev_month(self):
        if self.current_date.month == 1:
            self.current_date = self.current_date.replace(year=self.current_date.year - 1, month=12)
        else:
            self.current_date = self.current_date.replace(month=self.current_date.month - 1)
        self.render_calendar()

    def next_month(self):
        if self.current_date.month == 12:
            self.current_date = self.current_date.replace(year=self.current_date.year + 1, month=1)
        else:
            self.current_date = self.current_date.replace(month=self.current_date.month + 1)
        self.render_calendar()

    def load_habits(self):
        try:
            if os.path.exists("habits.json"):
                with open("habits.json", "r") as f:
                    self.habits = json.load(f)
        except Exception as e:
            print(f"Error loading habits: {e}")
            self.habits = []

    def save_habits(self):
        try:
            with open("habits.json", "w") as f:
                json.dump(self.habits, f)
            messagebox.showinfo("Success", "Habits saved successfully!")
        except Exception as e:
            messagebox.showerror("Error", f"Failed to save habits: {e}")

if __name__ == "__main__":
    root = tk.Tk()
    app = HabitTrackerApp(root)
    root.mainloop()
