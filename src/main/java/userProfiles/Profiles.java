package userProfiles;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Profiles {

    private ConcurrentMap<String, ProfileSettings> profiles;
    private static Profiles instance;

    private Profiles() {
        profiles = new ConcurrentHashMap<>();
    }

    public static Profiles getInstance() {
        if (instance == null) {
            return new Profiles();
        }
        return instance;
    }

    public void updateProfileSettings(String chatId, ProfileSettings profileSettings) {
        profiles.put(chatId, profileSettings);
    }

    public ProfileSettings getProfileSettings(String chatId) {
        return Optional.
                of(profiles.get(chatId)).
                orElse(new ProfileSettings());
    }

    public void removeProfileSettings(String chatId) {
        profiles.remove(chatId);
    }

    public ProfileSettings getDefaultProfileSettings() {
        return new ProfileSettings();
    }
}
