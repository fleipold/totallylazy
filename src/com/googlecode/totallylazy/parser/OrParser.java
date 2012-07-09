package com.googlecode.totallylazy.parser;

import com.googlecode.totallylazy.Segment;

import static com.googlecode.totallylazy.Unchecked.cast;

public class OrParser<A> extends AbstractParser<A> {
    private final Parser<? extends A> parserA;
    private final Parser<? extends A> parserB;

    private OrParser(Parser<? extends A> parserA, Parser<? extends A> parserB) {
        this.parserA = parserA;
        this.parserB = parserB;
    }

    public static <A> OrParser<A> or(Parser<? extends A> parserA, Parser<? extends A> parserB) {
        return new OrParser<A>(parserA, parserB);
    }

    @Override
    public String toString() {
        return String.format("%s or %s", parserA, parserB);
    }

    @Override
    public Result<A> parse(Segment<Character> characters) throws Exception {
        Result<? extends A> result = parserA.parse(characters);
        if(result instanceof Failure){
            return cast(parserB.parse(characters));
        }
        return cast(result);
    }
}
