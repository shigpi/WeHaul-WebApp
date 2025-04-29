document.addEventListener('DOMContentLoaded', function() {
    const citiesByProvince = {
        'Province 1': ['Biratnagar', 'Dharan', 'Itahari', 'Bhadrapur', 'Damak'],
        'Madhesh': ['Janakpur', 'Birgunj', 'Kalaiya', 'Jaleshwar', 'Malangwa'],
        'Bagmati Province': ['Kathmandu', 'Lalitpur', 'Bhaktapur', 'Hetauda', 'Banepa'],
        'Gandaki Province': ['Pokhara', 'Baglung', 'Gorkha', 'Besisahar', 'Kusma'],
        'Lumbini Province': ['Butwal', 'Nepalgunj', 'Tansen', 'Bhairahawa', 'Gulariya'],
        'Karnali Province': ['Birendranagar', 'Manma', 'Jumla', 'Dunai', 'Chandannath'],
        'Sudurpashchim Province': ['Dhangadhi', 'Mahendranagar', 'Tikapur', 'Dipayal', 'Baitadi']
    };

    function updateCities() {
        const provinceSelect = document.getElementById('province');
        const citySelect = document.getElementById('city');

        if (!provinceSelect || !citySelect) return; // Prevent errors if elements are missing

        const selectedProvince = provinceSelect.value;
        citySelect.innerHTML = '<option value="">Select City</option>';

        if (citiesByProvince[selectedProvince]) {
            citiesByProvince[selectedProvince].forEach(function(city) {
                const option = document.createElement('option');
                option.value = city;
                option.textContent = city;
                citySelect.appendChild(option);
            });
        }
    }

    const provinceSelect = document.getElementById('province');
    if (provinceSelect) {
        provinceSelect.addEventListener('change', updateCities);
    }

    const form = document.querySelector('form');
    if (form) {
        form.addEventListener('submit', function(e) {
            const password = document.getElementById('password');
            const confirmPassword = document.getElementById('confirmPassword');

            if (!password || !confirmPassword) return; // Prevent errors

            if (password.value !== confirmPassword.value) {
                e.preventDefault();
                alert('Passwords do not match!');
            }
        });
    }
});


