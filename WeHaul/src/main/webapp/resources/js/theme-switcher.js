document.addEventListener('DOMContentLoaded', () => {
    const themeSwitcher = document.getElementById('theme-switcher');
    const currentTheme = localStorage.getItem('theme') ? localStorage.getItem('theme') : null;

    if (currentTheme) {
        document.body.classList.add(currentTheme);
        if (currentTheme === 'dark-theme') {
            themeSwitcher.textContent = '☀️'; // Sun icon for light mode
        } else {
            themeSwitcher.textContent = '🌙'; // Moon icon for dark mode
        }
    } else {
         themeSwitcher.textContent = '🌙'; // Default to moon icon
    }

    themeSwitcher.addEventListener('click', () => {
        if (document.body.classList.contains('dark-theme')) {
            document.body.classList.remove('dark-theme');
            localStorage.setItem('theme', 'light-theme');
            themeSwitcher.textContent = '🌙';
        } else {
            document.body.classList.add('dark-theme');
            localStorage.setItem('theme', 'dark-theme');
            themeSwitcher.textContent = '☀️';
        }
    });
});
