package com.example.config;

import com.example.model.DiscussionQuestion;
import com.example.model.DiscussionReply;
import com.example.model.GardenProject;
import com.example.model.GardeningTip;
import com.example.repository.DiscussionQuestionRepository;
import com.example.repository.GardenProjectRepository;
import com.example.repository.GardeningTipRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final GardeningTipRepository tipRepository;
    private final DiscussionQuestionRepository questionRepository;
    private final GardenProjectRepository projectRepository;

    public DataInitializer(GardeningTipRepository tipRepository,
                           DiscussionQuestionRepository questionRepository,
                           GardenProjectRepository projectRepository) {
        this.tipRepository = tipRepository;
        this.questionRepository = questionRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public void run(String... args) {
        if (tipRepository.count() == 0) {
            initGardeningTips();
        }
        if (questionRepository.count() == 0) {
            initDiscussions();
        }
        if (projectRepository.count() == 0) {
            initProjects();
        }
    }

    private void initGardeningTips() {
        GardeningTip tip1 = new GardeningTip(
                "Natural Aphid Control Using Cold-Pressed Neem Oil Spray",
                "Pest & Disease Control",
                "A gentle, non-toxic horticultural solution to eliminate aphid and spider mite infestations without harming beneficial bees.",
                "Aphids multiply rapidly on tender new shoots and suck sap from your plants, causing curled leaves and sticky honeydew deposits.\n\n" +
                "### Ingredients:\n" +
                "- 1 Liter warm water\n" +
                "- 5 ml pure cold-pressed organic Neem Oil\n" +
                "- 2 ml mild liquid castile soap (acts as an emulsifier)\n\n" +
                "### Directions:\n" +
                "1. Mix the castile soap into warm water first, then vigorously shake in the neem oil until completely emulsified.\n" +
                "2. Spray early in the morning or late in the evening. Avoid spraying under hot direct midday sun to prevent leaf scorching.\n" +
                "3. Thoroughly cover both the upper and lower surfaces of leaves.\n" +
                "4. Repeat every 5 to 7 days until the pest population is entirely suppressed.",
                "Dr. Priya Sharma",
                "Beginner",
                "Roses, Hibiscus & Veggies",
                "https://images.unsplash.com/photo-1592417817098-8f3d6ef23117?auto=format&fit=crop&w=800&q=80"
        );
        tip1.setLikesCount(42);
        tipRepository.save(tip1);

        GardeningTip tip2 = new GardeningTip(
                "The Ultimate Potting Soil Recipe for Balcony Container Gardening",
                "Soil & Fertilizer",
                "Create a lightweight, moisture-retentive, aerated soil blend perfect for potted plants and balcony planters.",
                "Heavy garden soil compacts quickly in containers and suffocates roots. Potted plants demand high aeration and balanced moisture retention.\n\n" +
                "### The Golden 40-30-20-10 Ratio:\n" +
                "- **40% Washed Cocopeat / Peat Moss:** Provides water retention and lightweight structure.\n" +
                "- **30% Well-Rotted Vermicompost:** Rich organic macro and micro-nutrients.\n" +
                "- **20% Coarse Perlite or Pumice:** Maintains air pockets and stops soil compacting.\n" +
                "- **10% Garden Loam + 2 tbsp Neem Cake Powder:** Prevents soil-borne root nematodes and fungal pathogens.\n\n" +
                "Blend thoroughly, moisten lightly, and let rest for 24 hours before potting your seedlings.",
                "Ankush GardenGuru",
                "Intermediate",
                "Potted Houseplants & Herbs",
                "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?auto=format&fit=crop&w=800&q=80"
        );
        tip2.setLikesCount(35);
        tipRepository.save(tip2);

        GardeningTip tip3 = new GardeningTip(
                "How to Propagate Monsteras and Pothos in Clear Water",
                "Propagation",
                "Step-by-step tutorial on propagating tropical foliage from stem cuttings with healthy root nodes.",
                "Stem cutting propagation is one of the most rewarding ways to multiply your houseplant collection.\n\n" +
                "### Steps:\n" +
                "1. Locate an aerial root node on your Monstera or Pothos vine.\n" +
                "2. Use a sterile, sharp pruning shear to cut roughly 1-2 inches below the node at a 45-degree angle.\n" +
                "3. Place the stem in a clean glass jar filled with filtered room-temperature water, keeping leaves above the water level.\n" +
                "4. Position in bright, indirect light.\n" +
                "5. Change the water weekly to replenish oxygen levels and avoid algae growth.\n" +
                "6. Transplant into potting mix once secondary roots develop to 2-3 inches length.",
                "Flora Green",
                "Beginner",
                "Monstera, Pothos & Philodendron",
                "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?auto=format&fit=crop&w=800&q=80"
        );
        tip3.setLikesCount(58);
        tipRepository.save(tip3);

        GardeningTip tip4 = new GardeningTip(
                "Preventing Powdery Mildew and Fungal Leaf Blight in Monsoon",
                "Pest & Disease Control",
                "Practical ventilation and bio-fungicide practices to protect your garden during high humidity rainy periods.",
                "High relative humidity coupled with stagnant air creates an ideal breeding ground for powdery mildew, black spot, and fungal leaf spot.\n\n" +
                "### Essential Preventative Steps:\n" +
                "- **Ensure Air Circulation:** Prune overcrowded inner branches to allow breeze through the plant canopy.\n" +
                "- **Water Only the Base:** Never sprinkle water over foliage in the evening.\n" +
                "- **Baking Soda Fungicide:** Mix 1 teaspoon of baking soda and half teaspoon liquid soap into 1 Liter water. Mist plants bi-weekly as a gentle pH shield against fungal spores.",
                "Prof. Harsh Rai",
                "Intermediate",
                "Vegetables, Roses & Cucurbits",
                "https://images.unsplash.com/photo-1530836369250-ef72a3f5cda8?auto=format&fit=crop&w=800&q=80"
        );
        tip4.setLikesCount(29);
        tipRepository.save(tip4);

        GardeningTip tip5 = new GardeningTip(
                "Pruning and Trellising Cherry Tomatoes for High Yields",
                "Plant Care",
                "Maximize your homegrown tomato harvest by removing suckers and securing vines to vertical supports.",
                "Indeterminate tomato varieties produce endless foliage unless pruned. Here is how to direct energy into fruit production:\n\n" +
                "- **Identify Suckers:** Spot the 45-degree shoots growing in the crotch between the main stem and leaf branches.\n" +
                "- **Pinch Early:** When suckers are under 2 inches, snap them off cleanly with your fingers in the morning.\n" +
                "- **Support Heavily:** Tie the central vine loosely with garden twine to a wooden stake or wire trellis.\n" +
                "- **Mulch the Soil:** Add a 2-inch layer of dry straw around the base to keep soil moisture even and prevent blossom end rot.",
                "Rutwik Kumbhar",
                "Intermediate",
                "Cherry Tomatoes & Peppers",
                "https://images.unsplash.com/photo-1592841200221-a6898f307baa?auto=format&fit=crop&w=800&q=80"
        );
        tip5.setLikesCount(47);
        tipRepository.save(tip5);
    }

    private void initDiscussions() {
        DiscussionQuestion q1 = new DiscussionQuestion(
                "Why are my Monstera Deliciosa lower leaves turning yellow with brown spots?",
                "Plant Diseases",
                "Over the past two weeks, two of the lower mature leaves on my indoor Monstera have turned pale yellow, and today I noticed soft brown patches near the leaf edges. I water it once every 5 days and keep it 6 feet from an east-facing window. Is this root rot or nutrient deficiency?",
                "Kavita Deshmukh",
                "Monstera Deliciosa",
                "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?auto=format&fit=crop&w=800&q=80"
        );
        q1.setViewsCount(142);
        q1.setResolved(true);

        DiscussionReply r1 = new DiscussionReply(
                "Dr. Priya Sharma",
                "This is almost certainly early-stage overwatering leading to root suffocation. In indoor settings, watering every 5 days is typically too frequent unless the plant is in a terracotta pot in full sun. Check the soil depth with a wooden chopstick: only water when the top 2-3 inches are bone dry. Also ensure your pot has adequate drainage holes!",
                true
        );
        r1.setUpvotes(18);
        q1.addReply(r1);

        DiscussionReply r2 = new DiscussionReply(
                "Ankush GardenGuru",
                "Agree with Dr. Priya. Also, feel the brown patches: if they are soft/mushy, that confirms fungal edema from excess moisture. Trim off the completely yellow leaves with clean scissors and hold off watering for at least 10 days.",
                false
        );
        r2.setUpvotes(9);
        q1.addReply(r2);

        questionRepository.save(q1);

        DiscussionQuestion q2 = new DiscussionQuestion(
                "White powdery substance spreading over my hybrid tea rose leaves and buds",
                "Pest & Disease Control",
                "My rose bushes were flowering beautifully until last week when white powdery dust appeared on the young shoots and new buds. The infected leaves are now curling up and falling off. What fungicide works best without burning the flowers?",
                "Sunil Narang",
                "Hybrid Tea Roses",
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=800&q=80"
        );
        q2.setViewsCount(87);
        q2.setResolved(true);

        DiscussionReply r3 = new DiscussionReply(
                "Prof. Harsh Rai",
                "That is classic Powdery Mildew (Podosphaera pannosa). Prepare a mild bio-fungicide: dissolve 1 teaspoon potassium bicarbonate (or baking soda) and a few drops of dish soap in 1L water, and spray thoroughly in the morning. Also, prune the dense center of your rose bush so air can circulate freely.",
                true
        );
        r3.setUpvotes(12);
        q2.addReply(r3);

        questionRepository.save(q2);

        DiscussionQuestion q3 = new DiscussionQuestion(
                "What is the best vegetable crop for shallow balcony planters in winter?",
                "Indoor & Balcony",
                "I have three rectangular planter boxes that are only 6 inches deep on an apartment balcony receiving about 4 hours of morning sun. Which leafy greens or root vegetables will thrive in this depth?",
                "Neha Agarwal",
                "Winter Greens",
                null
        );
        q3.setViewsCount(65);
        q3.setResolved(false);

        DiscussionReply r4 = new DiscussionReply(
                "Rutwik Kumbhar",
                "With 6 inches depth and 4 hours of morning sun, leafy greens will do exceptionally well! Spinach (Palak), Coriander (Dhania), Fenugreek (Methi), and loose-leaf lettuce have shallow fibrous root systems and love cool weather. Avoid deep taproot carrots or large brassicas like cauliflower.",
                true
        );
        r4.setUpvotes(8);
        q3.addReply(r4);

        questionRepository.save(q3);

        DiscussionQuestion q4 = new DiscussionQuestion(
                "Tiny webbings and yellow speckled leaves on my Meyer Lemon plant",
                "Pest & Disease Control",
                "I noticed very fine spider-like silk webbings between the leaf petioles of my potted lemon tree, and the leaves have tiny pale pinprick dots all over. Are these red spider mites?",
                "Vikram Sen",
                "Citrus / Meyer Lemon",
                null
        );
        q4.setViewsCount(53);
        q4.setResolved(false);

        DiscussionReply r5 = new DiscussionReply(
                "Dr. Priya Sharma",
                "Yes, those fine webbings and stippled yellow dots are hallmark signs of spider mites! They thrive in hot, dry conditions. Immediately isolate the tree, give the entire canopy a strong spray with a hose to knock down the population, and follow up with cold-pressed neem oil or insecticidal soap.",
                true
        );
        r5.setUpvotes(6);
        q4.addReply(r5);

        questionRepository.save(q4);
    }

    private void initProjects() {
        GardenProject p1 = new GardenProject(
                "Balcony Mediterranean Culinary Herb Garden",
                "Growing fresh culinary herbs in terracotta pots on our apartment east-facing balcony. Focusing on Genovese Basil, English Thyme, Greek Oregano, and Rosemary.",
                "Culinary Herbs",
                "Apartment Balcony",
                "Flourishing",
                LocalDate.now().minusWeeks(6),
                LocalDate.now().plusWeeks(12),
                "Basil and Thyme have rooted wonderfully. Pinching tips weekly encourages bushy compact growth. Oregano pruned for kitchen use today.",
                "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?auto=format&fit=crop&w=800&q=80",
                "Rohan & Sneha"
        );
        projectRepository.save(p1);

        GardenProject p2 = new GardenProject(
                "Rooftop Heirloom Cherry Tomato & Chili Patch",
                "Setting up 5 large 15-gallon fabric grow bags with drip irrigation on our sunny rooftop for San Marzano tomatoes, cherry tomatoes, and Thai bird chilies.",
                "Tomatoes & Peppers",
                "Rooftop Garden",
                "In Progress",
                LocalDate.now().minusWeeks(3),
                LocalDate.now().plusWeeks(8),
                "Seedlings transplanted into grow bags with compost and perlite blend. Bamboo stakes installed. First yellow tomato blossoms appearing!",
                "https://images.unsplash.com/photo-1592841200221-a6898f307baa?auto=format&fit=crop&w=800&q=80",
                "Arun Mehta"
        );
        projectRepository.save(p2);

        GardenProject p3 = new GardenProject(
                "Indoor Air-Purifying Fern & Aroid Terrarium Corner",
                "Converting our home office nook into a humid green oasis featuring Bird's Nest Fern, Calathea Medallion, and trailing Epipremnum Aureum.",
                "Indoor Tropical Foliage",
                "Home Office Nook",
                "In Progress",
                LocalDate.now().minusWeeks(2),
                LocalDate.now().plusMonths(3),
                "Compact ultrasonic humidifier set to 60% relative humidity. Added full-spectrum LED clip grow light on a 10-hour daily timer.",
                "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?auto=format&fit=crop&w=800&q=80",
                "Maya Kapoor"
        );
        projectRepository.save(p3);

        GardenProject p4 = new GardenProject(
                "Raised Bed Winter Pollinator Garden",
                "Creating a seasonal pollinator bed with French Marigolds, Calendula, Petunias, and Sweet Alyssum to attract honeybees and predatory wasps.",
                "Pollinator Flowers",
                "Backyard Raised Beds",
                "Completed",
                LocalDate.now().minusMonths(2),
                LocalDate.now().minusDays(5),
                "Full bloom achieved! Marigolds have helped keep nematodes away from neighboring vegetable beds while honeybee activity has quadrupled.",
                "https://images.unsplash.com/photo-1530836369250-ef72a3f5cda8?auto=format&fit=crop&w=800&q=80",
                "Gurpreet Singh"
        );
        projectRepository.save(p4);
    }
}
