let stompClient = null;
let subscription = null;
let currentRoom = null;

const chatBox = document.getElementById("chat");

function log(text) {
    const p = document.createElement("div");
    p.textContent = text;
    chatBox.appendChild(p);
    chatBox.scrollTop = chatBox.scrollHeight;
}

document.getElementById("connectBtn").addEventListener("click", () => {
    const sender = document.getElementById("sender").value.trim();
    const room = document.getElementById("roomId").value.trim();
    if (!sender || !room) {
        alert("sender와 roomId를 입력하세요");
        return;
    }
    currentRoom = room;

    const socket = new SockJS("/ws/chat"); // 서버에서 등록한 endpoint
    stompClient = Stomp.over(socket);
    stompClient.debug = () => {};

    stompClient.connect({}, frame => {
        log("Connected");
        document.getElementById("connectBtn").disabled = true;
        document.getElementById("disconnectBtn").disabled = false;
        document.getElementById("sendBtn").disabled = false;

        const destination = "/topic/chat/" + currentRoom;

        subscription = stompClient.subscribe(destination, message => {
            const payload = JSON.parse(message.body);
            log(`${payload.sender}: ${payload.content}`);
        });
    });
});

document.getElementById("disconnectBtn").addEventListener("click", () => {
    if (subscription) subscription.unsubscribe();
    if (stompClient) stompClient.disconnect(() => log("Disconnected"));

    document.getElementById("connectBtn").disabled = false;
    document.getElementById("disconnectBtn").disabled = true;
    document.getElementById("sendBtn").disabled = true;
});

document.getElementById("sendBtn").addEventListener("click", () => {
    const sender = document.getElementById("sender").value;
    const content = document.getElementById("messageInput").value;

    if (!content.trim()) return;

    const dto = {
        sender: sender,
        content: content,
        roomId: Number(currentRoom)
    };

    stompClient.send("/app/chat/send", {}, JSON.stringify(dto));
    document.getElementById("messageInput").value = "";
});
