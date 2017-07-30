package razvan.extractor;


import junit.framework.Assert;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import razvan.extractor.utils.Utils;

public class TestToLoweCaseWords {

    @Test
    public void test_toLowerCaseWords() {
        String sentence1 = "They were not railway children to begin with."; // simple words
        List<String> expectedListForSentence1 = Arrays.asList("they", "were", "not", "railway",
                "children", "to", "begin", "with");
        Assert.assertEquals(expectedListForSentence1, Utils.getLowerCaseWords(sentence1));

        String sentence2 = "I don't suppose they had\n" +                                   // words with '
                "ever thought about railways except as a means of getting to Maskelyne\n" +
                "and Cook's";
        List<String> expectedListForSentence2 = Arrays.asList("i", "don't", "suppose", "they", "had",
                "ever", "thought", "about", "railways", "except", "as", "a", "means", "of",
                "getting", "to", "maskelyne", "and", "cook's");
        Assert.assertEquals(expectedListForSentence2, Utils.getLowerCaseWords(sentence2));
    }

}
