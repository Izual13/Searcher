package com.example.searcher;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SpringBootApplication(exclude = ThymeleafAutoConfiguration.class)
public class SearcherApplication {

    public static final int TAR_OFFSET = 0x101;

    public static void main(String[] args) {

        Resource resource = new ClassPathResource("sb.tar");
        try {
            InputStream resourceInputStream = resource.getInputStream();
            unpack(resourceInputStream.readAllBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        SpringApplication.run(SearcherApplication.class, args);
    }


    static boolean unpack(byte[] bytes) throws Exception {
        if (bytes == null || bytes.length == 0) {
            return false;
        }

        if (checkZip(bytes)) {
            unzip(new ByteArrayInputStream(bytes));
        } else if (checkTar(bytes)) {
            untar(new ByteArrayInputStream(bytes));
        } else {
            return false;
        }

        return true;
    }

    static boolean checkZip(byte[] bytes) {
        return bytes.length >= 4 && bytes[0] == 0x50 && bytes[1] == 0x4b && bytes[2] == 0x03 && bytes[3] == 0x04;
    }


    static boolean checkTar(byte[] bytes) {

        return bytes.length >= TAR_OFFSET + 8 && bytes[TAR_OFFSET] == 0x75 &&
                bytes[TAR_OFFSET + 1] == 0x73 &&
                bytes[TAR_OFFSET + 2] == 0x74 &&
                bytes[TAR_OFFSET + 3] == 0x61 &&
                bytes[TAR_OFFSET + 4] == 0x72 &&
                ((bytes[TAR_OFFSET + 5] == 0x00 && bytes[TAR_OFFSET + 6] == 0x30 && bytes[TAR_OFFSET + 7] == 0x30) ||
                        (bytes[TAR_OFFSET + 5] == 0x20 && bytes[TAR_OFFSET + 6] == 0x20 && bytes[TAR_OFFSET + 7] == 0x00));


    }


    static void unzip(InputStream is) throws Exception {
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {

            String fileName = zipEntry.getName();
            byte[] readAllBytes = zis.readAllBytes();
            if (!unpack(readAllBytes)) {
                parseBytes(fileName, readAllBytes);
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    private static void untar(InputStream is) throws Exception {
        try (TarArchiveInputStream fin = new TarArchiveInputStream(is)) {
            TarArchiveEntry entry;
            while ((entry = fin.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                String fileName = entry.getName();

                byte[] readAllBytes = fin.readAllBytes();
                if (!unpack(readAllBytes)) {
                    parseBytes(fileName, readAllBytes);
                }
            }
        }
    }

    private static AtomicInteger counter = new AtomicInteger(0);


    private static void parseBytes(String filename, byte[] bytes) {
        Optional.of(bytes)
                .map(String::new)
                .map(s -> s.split("\\n|\\\\n"))
                .stream()
                .flatMap(Arrays::stream)
                .parallel()
                .filter(p -> p.contains("password"))
                .forEach(s -> System.out.println(counter.incrementAndGet() + ") " + filename + ":\n" + s + "\n"));
    }


    @RestController
    static class TweetController {
        @GetMapping
        public Mono<String> getAllTweets() {

            return Mono.just("test");
        }


    }
}