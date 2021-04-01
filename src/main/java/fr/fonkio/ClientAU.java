package fr.fonkio;

import net.dv8tion.jda.api.entities.PrivateChannel;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class ClientAU extends WebSocketClient {

    CaptureEvent captureEvent;
    private BotData data;
    public ClientAU(URI serverUri, CaptureEvent captureEvent, BotData data) {
        super(serverUri);
        this.captureEvent = captureEvent;
        this.data = data;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        PrivateChannel pc = data.getAuthor().getUser().openPrivateChannel().complete();
        pc.sendMessage("Connecté");
    }

    @Override
    public void onMessage(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            switch (jsonObject.getInt("EventID")) {
                case 0:
                    captureEvent.state(new JSONObject(jsonObject.getString("EventData")));
                    break;
                case 1:
                    captureEvent.playerAction(new JSONObject(jsonObject.getString("EventData")));
                    break;
                case 2:
                    captureEvent.code(new JSONObject(jsonObject.getString("EventData")));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean b) {
        PrivateChannel pc = data.getAuthor().getUser().openPrivateChannel().complete();
        pc.sendMessage("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onError(Exception e) {
        PrivateChannel pc = data.getAuthor().getUser().openPrivateChannel().complete();
        pc.sendMessage("Erreur voir console");
        e.printStackTrace();
    }
}