package gd.utilities;

final class LabelGun
{
    private final String[] alphabet = {
            "", "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "X", "Y", "Z"
    };

    private int shotsFired = 0;

    public String shoot()
    {
        if (shotsFired > 26 * 25)
        {
            throw new RuntimeException("LabelGun is out of tape!");
        }

        var firstDigit = shotsFired % 25;
        var secondDigit = (shotsFired - firstDigit) / 25;

        shotsFired++;
        return alphabet[firstDigit+1] + alphabet[secondDigit];
    }
}
