package userProfiles;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.SneakyThrows;
import services.Shedule;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Profiles implements Serializable {

    private final static String GOOGLE_CREDENTIALS = System.getenv().get("GOOGLE_CREDENTIALS");
    private final static String GOOGLE_APPLICATION_NAME = System.getenv().get("GOOGLE_APPLICATION_NAME");
    private final static String FILE_ID = "profiles_" + System.getenv().get("botName") + ".dat";

    private static Profiles instance;
    private final ConcurrentMap<String, ProfileSettings> mapProfiles;

    private Profiles() {
        mapProfiles = new ConcurrentHashMap<>();
    }

    private static Profiles getFromAwsAmazon() {
        String bucketName = "profile-settings";
        String fileName = FILE_ID;
        Region region = Region.EU_CENTRAL_1;

        S3Client s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        if (s3Client.listBuckets(listBucketsRequest).buckets().stream().noneMatch(item -> item.name().equals(bucketName))) {
            return new Profiles();
        }
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try (ObjectInputStream objectInputStream = new ObjectInputStream(s3Client.getObject(getObjectRequest))) {
            return (Profiles) objectInputStream.readObject();
        } catch (Exception e) {
            return new Profiles();
        }
    }

    private static Profiles getFromGoogle() throws GeneralSecurityException, IOException {
        String googleCredentials = GOOGLE_CREDENTIALS;
        String googleApplicationName = GOOGLE_APPLICATION_NAME;

        if (Files.exists(Paths.get(googleCredentials))) {
            googleCredentials = new String(Files.readAllBytes(Paths.get(googleCredentials)));
        }

        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        NetHttpTransport netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();

        InputStream stringStream = new ByteArrayInputStream(googleCredentials.getBytes(StandardCharsets.UTF_8));
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(stringStream)
                .createScoped(DriveScopes.DRIVE_FILE);
        stringStream.close();

        Drive driveService = new Drive.Builder(netHttpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
                .setApplicationName(googleApplicationName)
                .build();

            /*List<com.google.api.services.drive.model.File> fileList = driveService
                    .files()
                    .list()
                    .execute()
                    .getFiles();

            for (com.google.api.services.drive.model.File file:fileList) {
                try {
                    driveService.files().delete(file.getId()).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/

        List<com.google.api.services.drive.model.File> fileList =
                driveService
                        .files()
                        .list()
                        .execute()
                        .getFiles()
                        .stream()
                        .filter(item -> item.getName().equals("profiles_" + System.getenv().get("botName") + ".dat"))
                        .collect(Collectors.toList());
        if (fileList.size() > 0) {
            String fileId = fileList.get(0).getId();
            try (ObjectInputStream objectInputStream = new ObjectInputStream(
                    driveService
                            .files()
                            .get(fileId)
                            .executeMediaAsInputStream())) {
                return (Profiles) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return new Profiles();
            }
        } else {
            return new Profiles();
        }
    }

    /**
     * get Profiles from serialized file or create new if file doesn't exist
     */
    public static Profiles getInstance() {
        if (instance == null) {
            if (System.getenv().get("AWS_ACCESS_KEY_ID") != null) {
                instance = getFromAwsAmazon();
            } else if (System.getenv().get("GOOGLE_CREDENTIALS") != null) {
                try {
                    instance = getFromGoogle();
                } catch (IOException | GeneralSecurityException e) {
                    e.printStackTrace();
                }
            } else {
                try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("src/main/resources/profiles.dat"))) {
                    instance = (Profiles) objectInputStream.readObject();
                } catch (Exception e) {
                    instance = new Profiles();
                }
            }
        }
        return instance;
    }

    /**
     * get ProfileSettings from map or create new if ProfileSettings doesn't exist, return it and put into map
     */
    public ProfileSettings getProfileSettings(String chatId) {
        return Optional
                .ofNullable(mapProfiles.get(chatId))
                .orElseGet(() -> getDefaultProfileSettings(chatId));
    }


    /**
     * get all ProfileSettings from map
     */
    public Map<String, ProfileSettings> getAllProfileSettings() {
        return new HashMap<>(mapProfiles);
    }

    private void updateProfileSettings(String chatId, ProfileSettings profileSettings) {
        mapProfiles.put(chatId, profileSettings);
    }

    private ProfileSettings getDefaultProfileSettings(String chatId) {
        ProfileSettings profileSettings = new ProfileSettings();
        updateProfileSettings(chatId, profileSettings);
        return profileSettings;
    }

    @SneakyThrows
    private void saveToAwsAmazon() {
        String bucketName = "profile-settings";
        String fileName = "profiles_" + System.getenv().get("botName") + ".dat";
        Region region = Region.EU_CENTRAL_1;

        S3Client s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        if (s3Client.listBuckets(listBucketsRequest).buckets().stream().noneMatch(item -> item.name().equals(bucketName))) {

            S3Waiter s3Waiter = s3Client.waiter();
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(bucketRequest);

            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            if (s3Waiter.waitUntilBucketExists(bucketRequestWait).matched().response().isEmpty()) {
                return;
            }
        }
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(instance);
            int size = outputStream.toByteArray().length;
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
                s3Client.putObject(objectRequest, RequestBody.fromInputStream(inputStream, size));
            }
        }

    }

    private void saveToGoogle() throws IOException, GeneralSecurityException {

        String googleCredentials = System.getenv().get("GOOGLE_CREDENTIALS");
        String googleApplicationName = System.getenv().get("GOOGLE_APPLICATION_NAME");
        String fileId = "profiles_" + System.getenv().get("botName") + ".dat";

        if (Files.exists(Paths.get(googleCredentials))) {
            googleCredentials = new String(Files.readAllBytes(Paths.get(googleCredentials)));
        }

        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        NetHttpTransport netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();

        InputStream stringStream = new ByteArrayInputStream(googleCredentials.getBytes(StandardCharsets.UTF_8));
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(stringStream)
                .createScoped(DriveScopes.DRIVE_FILE);
        stringStream.close();

        Drive driveService = new Drive.Builder(netHttpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
                .setApplicationName(googleApplicationName)
                .build();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(instance);
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
                InputStreamContent mediaContent = new InputStreamContent("application/octet-stream", inputStream);
                com.google.api.services.drive.model.File fileMeta = new com.google.api.services.drive.model.File();
                fileMeta.setName(fileId);

                List<com.google.api.services.drive.model.File> files = driveService
                        .files()
                        .list()
                        .execute()
                        .getFiles()
                        .stream()
                        .filter(item -> item.getName().equals(fileId))
                        .collect(Collectors.toList());

                if (files.size() > 0) {
                    driveService
                            .files()
                            .update(files.get(0).getId(), fileMeta, mediaContent)
                            .execute();
                } else {
                    driveService
                            .files()
                            .create(fileMeta, mediaContent)
                            .execute();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * save current Profiles to serialized file on shedule (every 5 minutes)
     */
    public void SchedulerSaveToFile() {
        ScheduledExecutorService timer = Shedule.getInstance().getScheduledExecutorService();
        Runnable task = () -> {
            if (System.getenv().get("AWS_ACCESS_KEY_ID") != null) {
                saveToAwsAmazon();
            } else if (System.getenv().get("GOOGLE_CREDENTIALS") != null) {
                try {
                    saveToGoogle();
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("src/main/resources/profiles.dat"))) {
                    objectOutputStream.writeObject(instance);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 1L, 5L * 60L, TimeUnit.SECONDS);
    }

    public void setProfileSettings(String chatId, ProfileSettings profileSettings) {
        updateProfileSettings(chatId, profileSettings);
    }
}