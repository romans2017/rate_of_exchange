package userProfiles;

import lombok.SneakyThrows;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Profiles implements Serializable {

    private static Profiles instance;
    private final ConcurrentMap<String, ProfileSettings> mapProfiles;

    private Profiles() {
        mapProfiles = new ConcurrentHashMap<>();
    }

    private static Profiles GetFromAwsAmazon() {
        String bucketName = "profile-settings";
        String fileName = "profiles_" + System.getenv().get("botName") + ".dat";
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

    /**
     * получает экземпляр Profiles из сериализованного файла или, если файла нет - создает новый
     */
    public static Profiles getInstance() {
        if (instance == null) {
            if (System.getenv().get("botName") == null) {
                try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("src/main/resources/profiles.dat"))) {
                    instance = (Profiles) objectInputStream.readObject();
                } catch (Exception e) {
                    instance = new Profiles();
                }
            } else {
                instance = GetFromAwsAmazon();
            }
        }
        return instance;
    }

    /**
     * получает ProfileSettings из мапы или, если нет - создает новый, возвращает его и добавляет в мапу
     */
    public ProfileSettings getProfileSettings(String chatId) {
        return Optional
                .ofNullable(mapProfiles.get(chatId))
                .orElseGet(() -> getDefaultProfileSettings(chatId));
    }


    /**
     * получает все ProfileSettings из мапы
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
    private void SaveToAwsAmazon() {
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

    /**
     * сохраняет текущий экземпляр Profiles в сериализованный файл по определенному расписанию (каждые 5 минут)
     */
    public void SchedulerSaveToFile() {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            @SneakyThrows
            public void run() {
                if (System.getenv().get("botName") == null) {
                    try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("src/main/resources/profiles.dat"))) {
                        objectOutputStream.writeObject(instance);
                    }
                } else {
                    SaveToAwsAmazon();
                }
            }
        }, 1000L, 5L * 60L * 1000L);

    }

    public void setProfileSettings(String chatId, ProfileSettings profileSettings) {
        updateProfileSettings(chatId, profileSettings);
    }
}