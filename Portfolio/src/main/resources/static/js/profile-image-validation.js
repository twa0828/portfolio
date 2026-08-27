document.addEventListener("DOMContentLoaded", () => {
    const maxImageSize = 2 * 1024 * 1024;

    document
        .querySelectorAll('input[type="file"][name="profileImage"]')
        .forEach(input => {
            input.addEventListener("change", () => {
                const file = input.files[0];

                if (file && file.size > maxImageSize) {
                    alert("画像は2MB以内を選択してください");
                    input.value = "";
                }
            });
        });
});
