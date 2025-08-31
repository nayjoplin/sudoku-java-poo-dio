package sudoku;

public class Space {
    private Integer real;    // valor digitado pelo usuário (pode ser null)
    private int expected;    // valor correto (1..9)
    private boolean fixed;   // pista fixa?

    public Space(int expected, boolean fixed) {
        this.expected = expected;
        this.fixed = fixed;
        this.real = fixed ? expected : null;
    }

    public boolean isCorrect() { return real != null && real == expected; }
    public Integer getReal() { return real; }
    public void setReal(Integer real) { if (!fixed) this.real = real; }
    public int getExpected() { return expected; }
    public boolean isFixed() { return fixed; }
}
