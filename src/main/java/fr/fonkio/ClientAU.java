package fr.fonkio;

import net.dv8tion.jda.api.entities.PrivateChannel;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class ClientAU extends WebSocketClient {

    AmauListener amauListener;

    public ClientAU(URI serverUri, AmauListener amauListener) {
        super(serverUri);
        this.amauListener = amauListener;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        PrivateChannel pc = amauListener.author.getUser().openPrivateChannel().complete();
        pc.sendMessage("Connecté");
    }

    @Override
    public void onMessage(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            System.out.println(jsonObject);
            switch (jsonObject.getInt("EventID")) {
                case 0:
                    amauListener.state(new JSONObject(jsonObject.getString("EventData")));
                    break;
                case 1:
                    amauListener.playerAction(new JSONObject(jsonObject.getString("EventData")));
                    break;
                case 2:
                    amauListener.code(new JSONObject(jsonObject.getString("EventData")));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean b) {
        PrivateChannel pc = amauListener.author.getUser().openPrivateChannel().complete();
        pc.sendMessage("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onError(Exception e) {
        PrivateChannel pc = amauListener.author.getUser().openPrivateChannel().complete();
        pc.sendMessage("Erreur voir console");
        e.printStackTrace();
    }
}