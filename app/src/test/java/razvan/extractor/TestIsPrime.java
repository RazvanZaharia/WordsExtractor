package razvan.extractor;

import junit.framework.Assert;

import org.junit.Test;

import razvan.extractor.utils.Utils;

public class TestIsPrime {

    @Test
    public void test_isPrime() {
        Assert.assertFalse(Utils.isPrime(0));
        Assert.assertFalse(Utils.isPrime(1));
        Assert.assertFalse(Utils.isPrime(2));
        Assert.assertFalse(Utils.isPrime(4));
        Assert.assertFalse(Utils.isPrime(6));
        Assert.assertTrue(Utils.isPrime(3));
        Assert.assertTrue(Utils.isPrime(5));
        Assert.assertTrue(Utils.isPrime(7));
    }

}
