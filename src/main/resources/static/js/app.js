// ==========================================================
// Sistema Pescaria — scripts da camada View
// ==========================================================

document.addEventListener('DOMContentLoaded', () => {

    // Pede confirmação antes de enviar formulários de exclusão.
    // Uso no HTML: <form ... data-confirm="Excluir este pescador?">
    document.querySelectorAll('form[data-confirm]').forEach((form) => {
        form.addEventListener('submit', (evento) => {
            if (!window.confirm(form.dataset.confirm)) {
                evento.preventDefault();
            }
        });
    });

    // Esconde as mensagens de sucesso depois de alguns segundos.
    // Mensagens de erro e de aviso ficam na tela até o usuário sair da página.
    document.querySelectorAll('.flash:not(.erro):not(.aviso)').forEach((mensagem) => {
        setTimeout(() => {
            mensagem.style.transition = 'opacity .4s';
            mensagem.style.opacity = '0';
            setTimeout(() => mensagem.remove(), 400);
        }, 4000);
    });
});
