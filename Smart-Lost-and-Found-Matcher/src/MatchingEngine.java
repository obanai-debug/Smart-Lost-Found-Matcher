import java.util.*;

public class MatchingEngine {

    private Set<String> getKeywords(String text) {

        Set<String> keywords = new HashSet<>();

        if (text == null || text.isBlank()) {
            return keywords;
        }

        String cleaned = text.toLowerCase()
                .replaceAll("[^a-z0-9 ]", " ");

        String[] words = cleaned.split("\\s+");

        for (String word : words) {
            if (word.length() > 2) {
                keywords.add(word);
            }
        }

        return keywords;
    }

    public int calculateScore(LostItem lost, FoundItem found) {

        int score = 0;

        if (lost.getCategory().equalsIgnoreCase(found.getCategory())) {
            score += 30;
        }

        if (lost.getName().equalsIgnoreCase(found.getName())) {
            score += 30;
        } else if (
                lost.getName().toLowerCase().contains(
                        found.getName().toLowerCase()
                )
                || found.getName().toLowerCase().contains(
                        lost.getName().toLowerCase()
                )
        ) {
            score += 20;
        }

        if (lost.getLocation().equalsIgnoreCase(found.getLocation())) {
            score += 20;
        }

        Set<String> lostWords =
                getKeywords(lost.getDescription());

        Set<String> foundWords =
                getKeywords(found.getDescription());

        Set<String> commonWords =
                new HashSet<>(lostWords);

        commonWords.retainAll(foundWords);

        Set<String> allWords =
                new HashSet<>(lostWords);

        allWords.addAll(foundWords);

        if (!allWords.isEmpty()) {
            score += Math.round(
                    20f * commonWords.size() / allWords.size()
            );
        }

        return score;
    }

    public List<String> findMatches(
            List<LostItem> lostItems,
            List<FoundItem> foundItems) {

        Map<String, Integer> matches = new HashMap<>();

        for (LostItem lost : lostItems) {

            for (FoundItem found : foundItems) {

                int score = calculateScore(lost, found);

                if (score >= 40) {

                    String result =
                            lost.getName() + " <-> " +
                            found.getName() + " | Match: " +
                            score + "%";

                    matches.put(result, score);
                }
            }
        }

        List<String> results =
                new ArrayList<>(matches.keySet());

        results.sort(
                (a, b) -> matches.get(b).compareTo(matches.get(a))
        );

        return results;
    }
}