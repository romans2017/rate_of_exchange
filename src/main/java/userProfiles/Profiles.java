package userProfiles;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Profiles {

    private final ConcurrentMap<String, ProfileSettings> mapProfiles;
    private static Profiles instance;

    private Profiles() {
        mapProfiles = new ConcurrentHashMap<>();
    }

    public static Profiles getInstance() {
        if (instance == null) {
            return new Profiles();
        }
        return instance;
    }

    public void updateProfileSettings(String chatId, ProfileSettings profileSettings) {
        mapProfiles.put(chatId, profileSettings);
    }

    public ProfileSettings getProfileSettings(String chatId) {

        return Optional.ofNullable(mapProfiles.get(chatId)).orElse(getDefaultProfileSettings(chatId));
    }

    public ProfileSettings getDefaultProfileSettings(String chatId) {

        ProfileSettings profileSettings = new ProfileSettings();
        updateProfileSettings(chatId, profileSettings);
        return profileSettings;
    }
}
