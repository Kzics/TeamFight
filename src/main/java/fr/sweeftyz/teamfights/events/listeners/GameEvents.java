package fr.sweeftyz.teamfights.events.listeners;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.events.listeners.custom.GameStartEvent;
import fr.sweeftyz.teamfights.requests.ApiClient;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.stream.Collectors;

public class GameEvents implements Listener {


    private final Main instance;
    public GameEvents(final Main instance){
        this.instance = instance;
    }

    @EventHandler
    public void onStart(GameStartEvent e){

        List<String> blueTeam = e.getBlueTeam()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList());

        List<String> redTeam = e.getRedTeam()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList());


        String json = String.format("{\"id\":%d,\"startedTime\":%d,\"blueTeam\":[\"%s\"],\"redTeam\":[\"%s\"]}", 17, System.currentTimeMillis(), String.join("\", \"", blueTeam), String.join("\", \"", redTeam));
        instance.getApiClient().sendPostRequest("http://localhost:8080/create", json, new ApiClient.ApiCallback<String>() {
            @Override
            public void onSuccess(String response) {
                System.out.println("Sucessfully created game.");
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
