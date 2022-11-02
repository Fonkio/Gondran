package fr.fonkio.utils;

import fr.fonkio.json.JSONReader;
import fr.fonkio.json.JSONWriter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class Configuration {

    private final JSONObject jsonObject;
    private final File file;

    public Configuration(String path) throws IOException {
        this.file = new File(path);
        if (this.file.exists()) {
            this.jsonObject = new JSONReader(this.file).toJSONObject();
        } else {
            jsonObject = new JSONObject();
        }
    }

   public JSONObject newServer() {
        JSONObject jo = new JSONObject();
        jo.put(ConfigurationEnum.TAG_ROLE.getKey(), ConfigurationEnum.TAG_ROLE.getDefaultValue());
        return jo;
   }

    public String getToken() {
        if(!jsonObject.has(ConfigurationEnum.TOKEN.getKey())) {
            jsonObject.put(ConfigurationEnum.TOKEN.getKey(), ConfigurationEnum.TOKEN.getDefaultValue());
            save();
        }
        return jsonObject.getString(ConfigurationEnum.TOKEN.getKey());
    }

    /**
     * @param guildId Id de la Guild à récupérer
     * @return Le JSONObject du la Guild, il en créé un si il ne trouve pas le serveur dans la config
     */
    private JSONObject getServerConfig(String guildId) {
        if(!jsonObject.has(guildId)) {
            jsonObject.put(guildId, newServer());
            save();
        }
        return jsonObject.getJSONObject(guildId);
    }


    public String getGuildConfig(String guildId, ConfigurationEnum key) {
        String confString = "";
        try {
            confString = getServerConfig(guildId).getString(key.getKey());
        } catch (JSONException e) {
            confString = key.getDefaultValue();
            setGuildConfig(guildId, key, confString);
        }
        return confString;
    }

    public void setGuildConfig(String guildId, ConfigurationEnum key, String value) {
        getServerConfig(guildId).put(key.getKey(), value);
        save();
    }

    public void save() {
        try (JSONWriter writer = new JSONWriter(file)) {
            writer.write(this.jsonObject);
            writer.flush();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }



}
