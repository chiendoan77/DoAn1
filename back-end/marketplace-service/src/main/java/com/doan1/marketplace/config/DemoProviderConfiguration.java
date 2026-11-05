package com.doan1.marketplace.config;

import com.doan1.marketplace.entity.ProviderProfile;
import com.doan1.marketplace.enums.ProviderStatus;
import com.doan1.marketplace.repository.ProviderProfileRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Configuration
@ConditionalOnProperty(name = "app.seed-demo-providers", havingValue = "true")
public class DemoProviderConfiguration {
    private static final String[] CLEANING_IMAGES = {
            "https://images.unsplash.com/photo-1581578731548-c64695cc6952",
            "https://images.unsplash.com/photo-1527515637462-cff94eecc1ac",
            "https://images.unsplash.com/photo-1585421514284-efb74c2b69ba",
            "https://images.unsplash.com/photo-1556911220-bff31c812dba"
    };
    private static final String[] REPAIR_IMAGES = {
            "https://images.unsplash.com/photo-1621905251918-48416bd8575a",
            "https://images.unsplash.com/photo-1504148455328-c376907d081c",
            "https://images.unsplash.com/photo-1581092921461-eab62e97a780",
            "https://images.unsplash.com/photo-1581783898377-1c85bf937427"
    };
    private static final String[] DISTRICTS = {
            "Quận Cầu Giấy, Hà Nội", "Quận Đống Đa, Hà Nội", "Quận Thanh Xuân, Hà Nội",
            "Quận Bình Thạnh, TP. Hồ Chí Minh", "Quận 3, TP. Hồ Chí Minh",
            "Quận Hải Châu, Đà Nẵng", "Quận Sơn Trà, Đà Nẵng"
    };

    @Bean
    CommandLineRunner seedDemoProviders(ProviderProfileRepository providers) {
        return args -> {
            if (providers.existsByDemoFixtureTrue()) {
                return;
            }
            List<ProviderProfile> fixtures = new ArrayList<>();
            addCategoryFixtures(fixtures, "cleaning", "Đội Chăm Nhà", CLEANING_IMAGES,
                    new double[]{5.0, 4.9, 4.8, 4.8, 4.7, 4.6, 4.5, 4.4, 4.3, 4.2});
            addCategoryFixtures(fixtures, "repair", "Đội Sửa Nhà", REPAIR_IMAGES,
                    new double[]{5.0, 4.9, 4.8, 4.7, 4.7, 4.6, 4.5, 4.4, 4.3, 4.1});
            providers.saveAll(fixtures);
        };
    }

    private static void addCategoryFixtures(
            List<ProviderProfile> fixtures,
            String categoryId,
            String namePrefix,
            String[] images,
            double[] ratings) {
        for (int index = 0; index < ratings.length; index++) {
            int number = index + 1;
            fixtures.add(new ProviderProfile(
                    UUID.randomUUID(),
                    namePrefix + " " + String.format("%02d", number),
                    Set.of(categoryId),
                    DISTRICTS[index % DISTRICTS.length],
                    images[index % images.length] + "?auto=format&fit=crop&w=960&q=80",
                    "Hồ sơ mẫu dùng để xem thử danh sách nhà cung cấp.",
                    ProviderStatus.APPROVED,
                    ratings[index],
                    12 + (number * 3),
                    true));
        }
    }
}
