package com.alex.irminsul.config;

import com.alex.irminsul.entity.*;
import com.alex.irminsul.repository.GameRepository;
import com.alex.irminsul.repository.PlatformRepository;
import com.alex.irminsul.repository.UserGameRepository;
import com.alex.irminsul.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataInitializer implements CommandLineRunner {

    private final GameRepository gameRepository;
    private final PlatformRepository platformRepository;
    private final UserRepository userRepository;
    private final UserGameRepository userGameRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(GameRepository gameRepository,
                           PlatformRepository platformRepository,
                           UserRepository userRepository,
                           UserGameRepository userGameRepository,
                           PasswordEncoder passwordEncoder) {
        this.gameRepository = gameRepository;
        this.platformRepository = platformRepository;
        this.userRepository = userRepository;
        this.userGameRepository = userGameRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (gameRepository.count() == 0) {
            seedDatabase();
        }
    }

    private void seedDatabase() {

        // ==========================================
        // 1. PLATFORMS
        // ==========================================
        Platform pc = createPlatform("PC");
        Platform ps5 = createPlatform("PlayStation 5");
        Platform ps4 = createPlatform("PlayStation 4");
        Platform ps3 = createPlatform("PlayStation 3");
        Platform ps2 = createPlatform("PlayStation 2");
        Platform nswitch = createPlatform("Nintendo Switch");
        Platform gba = createPlatform("Game Boy Advance");
        Platform nds = createPlatform("Nintendo DS");

        platformRepository.saveAll(List.of(pc, ps5, ps4, ps3, ps2, nswitch, gba, nds));

        // ==========================================
        // 2. USERS
        // ==========================================
        User alex = createUser("alex", "alex@irminsul.com", "alex12345", Role.ADMIN);
        User andrea = createUser("andrea", "andrea@irminsul.com", "andrea12345", Role.USER);
        User koda = createUser("koda", "koda@irminsul.com", "koda12345", Role.USER);
        User nif = createUser("nif", "nif@irminsul.com", "nif12345", Role.USER);
        User buny = createUser("buny", "buny@irminsul.com", "buny12345", Role.USER);
        User canela = createUser("canela", "canela@irminsul.com", "canela12345", Role.USER);
        User agro = createUser("agro", "agro@irminsul.com", "agro12345", Role.USER);
        User hyo = createUser("hyo", "hyo@irminsul.com", "hyo12345", Role.USER);
        User mina = createUser("mina", "mina@irminsul.com", "mina12345", Role.USER);
        User chispa = createUser("chispa", "chispa@irminsul.com", "chispa12345", Role.USER);
        User tara = createUser("tara", "tara@irminsul.com", "tara12345", Role.USER);
        User meteor = createUser("meteor", "meteor@irminsul.com", "meteor12345", Role.USER);
        User apocalyptus = createUser("apocalyptus", "apocalyptus@irminsul.com", "apocalyptus12345", Role.USER);
        User eluvian = createUser("eluvian", "eluvian@irminsul.com", "eluvian12345", Role.USER);

        userRepository.saveAll(List.of(
                alex, andrea, koda, nif, buny, canela, agro, hyo,
                mina, chispa, tara, meteor, apocalyptus, eluvian
        ));

        // ==========================================
        // 3. GAMES
        // ==========================================
        Game wow = createGame("World of Warcraft", "The definitive MMORPG set in the rich fantasy universe of Azeroth.", LocalDate.of(2004, 11, 23), "https://images.igdb.com/igdb/image/upload/t_cover_big/co2l7z.webp", Genre.RPG, Set.of(pc));
        Game ds1 = createGame("Dark Souls", "Prepare to die in the brutal, interconnected world of Lordran.", LocalDate.of(2011, 9, 22), "https://images.igdb.com/igdb/image/upload/t_cover_big/co1x78.webp", Genre.RPG, Set.of(pc, ps3, ps4, nswitch));
        Game ds2 = createGame("Dark Souls 2", "Journey through the cursed kingdom of Drangleic to seek a cure for the undead curse.", LocalDate.of(2014, 3, 11), "https://images.igdb.com/igdb/image/upload/t_cover_big/cobysr.webp", Genre.RPG, Set.of(pc, ps3, ps4));
        Game ds3 = createGame("Dark Souls 3", "As fires fade and the world falls into ruin, journey into a universe filled with colossal enemies.", LocalDate.of(2016, 3, 24), "https://images.igdb.com/igdb/image/upload/t_cover_big/cob9ed.webp", Genre.RPG, Set.of(pc, ps4));
        Game bloodborne = createGame("Bloodborne", "Face your nightmares in the cursed, gothic city of Yharnam.", LocalDate.of(2015, 3, 24), "https://images.igdb.com/igdb/image/upload/t_cover_big/cob99l.webp", Genre.RPG, Set.of(ps4, ps5));
        Game sekiro = createGame("Sekiro", "Carve your own clever path to vengeance in feudal Japan as the one-armed wolf.", LocalDate.of(2019, 3, 22), "https://images.igdb.com/igdb/image/upload/t_cover_big/co2a23.webp", Genre.ACTION, Set.of(pc, ps4));
        Game eldenRing = createGame("Elden Ring", "Rise, Tarnished, and be guided by grace to brandish the power of the Elden Ring.", LocalDate.of(2022, 2, 25), "https://images.igdb.com/igdb/image/upload/t_cover_big/co4jni.webp", Genre.RPG, Set.of(pc, ps4, ps5));
        Game mhWorld = createGame("Monster Hunter World", "Battle gigantic monsters in epic locales as a hunter tracking the Elder Dragons.", LocalDate.of(2018, 1, 26), "https://images.igdb.com/igdb/image/upload/t_cover_big/co1rst.webp", Genre.ACTION, Set.of(pc, ps4));
        Game mhRise = createGame("Monster Hunter Rise", "Set in the ninja-inspired Kamura Village, explore lush ecosystems and battle fearsome monsters.", LocalDate.of(2021, 3, 26), "https://images.igdb.com/igdb/image/upload/t_cover_big/co3uzk.webp", Genre.ACTION, Set.of(pc, ps4, ps5, nswitch));
        Game hollowKnight = createGame("Hollow Knight", "An epic, hand-drawn 2D action adventure through a vast ruined kingdom of insects.", LocalDate.of(2017, 2, 24), "https://images.igdb.com/igdb/image/upload/t_cover_big/cobfzp.webp", Genre.PLATFORMER, Set.of(pc, ps4, nswitch));
        Game silksong = createGame("Hollow Knight: Silksong", "Discover a vast, haunted kingdom in the sequel to the award-winning Hollow Knight.", LocalDate.of(2025, 9, 4), "https://images.igdb.com/igdb/image/upload/t_cover_big/cobebu.webp", Genre.PLATFORMER, Set.of(pc, ps4, ps5, nswitch));
        Game hades = createGame("Hades", "Defy the god of the dead as you hack and slash out of the Underworld.", LocalDate.of(2020, 9, 17), "https://images.igdb.com/igdb/image/upload/t_cover_big/cob9kr.webp", Genre.ACTION, Set.of(pc, ps4, ps5, nswitch));
        Game slayTheSpire = createGame("Slay the Spire", "The benchmark deck-building roguelike combining card games and tactical RPG combat.", LocalDate.of(2019, 1, 23), "https://images.igdb.com/igdb/image/upload/t_cover_big/co1iyf.webp", Genre.STRATEGY, Set.of(pc, ps4, nswitch));
        Game cuphead = createGame("Cuphead", "A classic run and gun action game heavily focused on boss battles inspired by 1930s cartoons.", LocalDate.of(2017, 9, 29), "https://images.igdb.com/igdb/image/upload/t_cover_big/co62ao.webp", Genre.PLATFORMER, Set.of(pc, ps4, nswitch));
        Game deadCells = createGame("Dead Cells", "A rogue-lite, metroidvania inspired action-platformer.", LocalDate.of(2018, 8, 7), "https://images.igdb.com/igdb/image/upload/t_cover_big/co7jfv.webp", Genre.ACTION, Set.of(pc, ps4, nswitch));
        Game skyrim = createGame("Skyrim", "The open-world masterpiece by Bethesda set in the cold mountains of Tamriel.", LocalDate.of(2011, 11, 11), "https://images.igdb.com/igdb/image/upload/t_cover_big/cocs1l.webp", Genre.RPG, Set.of(pc, ps3, ps4, ps5, nswitch));
        Game genshin = createGame("Genshin Impact", "Embark on a journey across Teyvat to seek answers from The Seven.", LocalDate.of(2020, 9, 28), "https://images.igdb.com/igdb/image/upload/t_cover_big/coa9dy.webp", Genre.ADVENTURE, Set.of(pc, ps4, ps5));
        Game smash = createGame("Super Smash Bros Ultimate", "Legendary game worlds and fighters collide in the ultimate showdown.", LocalDate.of(2018, 12, 7), "https://images.igdb.com/igdb/image/upload/t_cover_big/co2255.webp", Genre.FIGHTING, Set.of(nswitch));
        Game animalCrossing = createGame("Animal Crossing: New Horizons", "Escape to a deserted island and create your own paradise as you explore and customize.", LocalDate.of(2020, 3, 20), "https://images.igdb.com/igdb/image/upload/t_cover_big/co3wls.webp", Genre.SIMULATION, Set.of(nswitch));
        Game goldenSun = createGame("Golden Sun", "Control the power of Alchemy in this landmark GBA JRPG adventure.", LocalDate.of(2001, 8, 1), "https://images.igdb.com/igdb/image/upload/t_cover_big/co1uca.webp", Genre.RPG, Set.of(gba));
        Game bioshock = createGame("Bioshock", "Explore the underwater dystopian city of Rapture in this narrative FPS classic.", LocalDate.of(2007, 8, 21), "https://images.igdb.com/igdb/image/upload/t_cover_big/co2mli.webp", Genre.ACTION, Set.of(pc, ps3, ps4, nswitch));
        Game zeldaBotw = createGame("The Legend of Zelda: Breath of the Wild", "Step into a world of discovery, exploration, and adventure in Hyrule.", LocalDate.of(2017, 3, 3), "https://images.igdb.com/igdb/image/upload/t_cover_big/co3p2d.webp", Genre.ADVENTURE, Set.of(nswitch));
        Game zeldaTotk = createGame("The Legend of Zelda: Tears of the Kingdom", "Soar above the clouds and explore the depths of Hyrule in the epic sequel to Breath of the Wild.", LocalDate.of(2023, 5, 12), "https://images.igdb.com/igdb/image/upload/t_cover_big/co5vmg.webp", Genre.ADVENTURE, Set.of(nswitch));
        Game pokemonEmerald = createGame("Pokémon Emerald", "The definitive Hoenn adventure featuring Rayquaza, Kyogre, and Groudon.", LocalDate.of(2004, 9, 16), "https://images.igdb.com/igdb/image/upload/t_cover_big/co1zhr.webp", Genre.RPG, Set.of(gba));
        Game gtaSa = createGame("GTA San Andreas", "Carl Johnson returns to Los Santos in a sprawling 90s gangland saga.", LocalDate.of(2004, 10, 26), "https://images.igdb.com/igdb/image/upload/t_cover_big/co2lb9.webp", Genre.ACTION, Set.of(pc, ps2, ps3, ps4));

        gameRepository.saveAll(List.of(
                wow, ds1, ds2, ds3, bloodborne, sekiro, eldenRing, mhWorld, mhRise,
                hollowKnight, silksong, hades, slayTheSpire, cuphead, deadCells,
                skyrim, genshin, smash, animalCrossing, goldenSun, bioshock,
                zeldaBotw, zeldaTotk, pokemonEmerald, gtaSa
        ));

        // ==========================================
        // 4. USER-GAME
        // ==========================================

        createUserGame(alex, eldenRing, Status.COMPLETED, 10, "GOTY of the year.");
        createUserGame(alex, bloodborne, Status.COMPLETED, 9, "Fear the Old Blood.");
        createUserGame(alex, wow, Status.PLAYING, 10, "Better than life.");

        createUserGame(andrea, animalCrossing, Status.PLAYING, 8, "Over 400 hours on my island! Extremely relaxing.");
        createUserGame(andrea, zeldaTotk, Status.COMPLETED, 9, "Building mechanics with Ultrahand are amazing.");
        createUserGame(andrea, hollowKnight, Status.DROPPED, 7, "Beautiful art and music, but got lost and frustrated.");
        createUserGame(andrea, slayTheSpire, Status.PLAYING, 6, "Stucked on A3 with the Defect");

        createUserGame(koda, mhWorld, Status.COMPLETED, 9, "Hunting with friends is unmatched.");
        createUserGame(koda, slayTheSpire, Status.PLAYING, 9, "Trying to beat Ascension 20 with the Silent.");
        createUserGame(koda, deadCells, Status.COMPLETED, 8, "Fast, fluid, very fun weapon combos.");

        createUserGame(canela, pokemonEmerald, Status.COMPLETED, 8, "The Battle Frontier in Hoenn is legendary.");
        createUserGame(canela, goldenSun, Status.COMPLETED, 8, "One of the best battle themes and djinn system ever.");
        createUserGame(canela, wow, Status.BACKLOG, null, "Might resubscribe next month.");

        createUserGame(nif, cuphead, Status.COMPLETED, 9, "Hard as nails, but visual style is art.");
        createUserGame(nif, ds1, Status.COMPLETED, 8, "World connectivity in the first half is genius.");
        createUserGame(nif, bioshock, Status.COMPLETED, 9, "Would you kindly play this masterpiece?");
        createUserGame(nif, slayTheSpire, Status.COMPLETED, 7, "EZPZ.");

        createUserGame(agro, eldenRing, Status.COMPLETED, 9, "Challenging endgame bosses, but unforgettable.");
        createUserGame(agro, gtaSa, Status.COMPLETED, 9, "All we had to do was follow the damn train CJ!");
    }

    private Platform createPlatform(String name) {
        Platform platform = new Platform();
        platform.setName(name);
        return platform;
    }

    private User createUser(String username, String email, String rawPassword, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email.toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return user;
    }

    private Game createGame(String title, String description, LocalDate releaseDate,
                            String coverUrl, Genre genre, Set<Platform> platforms) {
        Game game = new Game();
        game.setTitle(title);
        game.setDescription(description);
        game.setReleaseDate(releaseDate);
        game.setCoverUrl(coverUrl);
        game.setGenre(genre);
        game.setPlatforms(platforms);
        return game;
    }

    private void createUserGame(User user, Game game, Status status, Integer rating, String notes) {
        UserGame userGame = new UserGame();
        userGame.setUser(user);
        userGame.setGame(game);
        userGame.setStatus(status);
        userGame.setRating(rating);
        userGame.setNotes(notes);
        userGameRepository.save(userGame);
    }
}