package com.newswebsite.config;

import com.newswebsite.model.Category;
import com.newswebsite.model.Comment;
import com.newswebsite.model.News;
import com.newswebsite.model.User;
import com.newswebsite.repository.CategoryRepository;
import com.newswebsite.repository.CommentRepository;
import com.newswebsite.repository.NewsRepository;
import com.newswebsite.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final CategoryRepository categoryRepository;
    private final NewsRepository newsRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CategoryRepository categoryRepository,
                           NewsRepository newsRepository,
                           CommentRepository commentRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.newsRepository = newsRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            logger.info("Database already initialized, skipping seed data.");
            return;
        }

        logger.info("Seeding initial categories, news items, and users...");

        // 1. Create Categories
        Category sports = categoryRepository.save(new Category("Sports", "Athletic events, matches, and sports updates"));
        Category entertainment = categoryRepository.save(new Category("Entertainment", "Cinema, music, celebrity culture, and pop trends"));
        Category technology = categoryRepository.save(new Category("Technology", "Computing, AI, gadgets, and digital innovations"));
        Category worldNews = categoryRepository.save(new Category("World News", "Global affairs, geopolitics, and international relations"));

        // 2. Create Users
        User readerKamal = userRepository.save(new User(
                "reader_kamal",
                "kamal@example.com",
                passwordEncoder.encode("password123"),
                "ROLE_READER"
        ));

        User sportsFan = userRepository.save(new User(
                "sports_fan99",
                "fan99@example.com",
                passwordEncoder.encode("password123"),
                "ROLE_READER"
        ));

        // 3. Create News Items
        // Example: "Sl lost" relates to BOTH Sports and Entertainment
        News slLost = new News(
                "Sl lost",
                "Dramatic defeat sparks nationwide entertainment and sports commentary as final over slips away.",
                "The national cricket team suffered a nail-biting defeat in the final over of yesterday's thriller. While sports analysts dissect bowling strategies, the dramatic post-match press conference and unexpected celebrity reactions have made it the top trending topic across sports talk shows and entertainment channels alike.",
                "Nuwan Perera"
        );
        Set<Category> slLostCategories = new HashSet<>();
        slLostCategories.add(sports);
        slLostCategories.add(entertainment);
        slLost.setCategories(slLostCategories);
        News savedSlLost = newsRepository.save(slLost);

        // Sports only news
        News championship = new News(
                "Championship Finals Scheduled for This Sunday",
                "Top regional teams gear up for high-stakes clash this coming weekend.",
                "The premier regional athletic championships reach their climax this Sunday at the National Stadium. Over 30,000 spectators are expected to attend what promises to be one of the most competitive finishes in recent history.",
                "Samantha Dias"
        );
        Set<Category> champCategories = new HashSet<>();
        champCategories.add(sports);
        championship.setCategories(champCategories);
        newsRepository.save(championship);

        // Entertainment only news
        News movie = new News(
                "Blockbuster Film Breaks Global Opening Weekend Box Office Records",
                "Record-smashing box office figures stunned industry insiders worldwide.",
                "The newest cinematic installment has surpassed all projections, grossing over $250 million across its debut weekend. Critics praise the groundbreaking visual effects and captivating ensemble performances.",
                "Arun Kumar"
        );
        Set<Category> movieCategories = new HashSet<>();
        movieCategories.add(entertainment);
        movie.setCategories(movieCategories);
        newsRepository.save(movie);

        // Technology news
        News aiNews = new News(
                "Next-Gen AI Systems Transform Modern Healthcare Diagnostics",
                "Hospitals adopt state-of-the-art neural networks to assist clinical specialists.",
                "Pioneering artificial intelligence algorithms are demonstrating unprecedented accuracy in early medical screenings, reducing patient diagnostic wait times and aiding healthcare professionals in complex cases.",
                "Elena Rostova"
        );
        Set<Category> aiCategories = new HashSet<>();
        aiCategories.add(technology);
        aiNews.setCategories(aiCategories);
        newsRepository.save(aiNews);

        // 4. Initial comments on "Sl lost"
        commentRepository.save(new Comment(
                savedSlLost,
                readerKamal,
                "reader_kamal",
                "Hard luck! The middle overs were good, but the death overs cost us the game."
        ));

        commentRepository.save(new Comment(
                savedSlLost,
                sportsFan,
                "sports_fan99",
                "Entertaining game regardless, kept everyone on the edge of their seats!"
        ));

        logger.info("Sample seed data created successfully!");
    }
}
