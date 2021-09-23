package ru.pixelmongo.pixelmongo.exceptions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DonatePackTokenProcessExcetion extends DonatePackTokenException{

    private static final long serialVersionUID = -423528076491913250L;

    private final List<String> args;

    public DonatePackTokenProcessExcetion(String tokenName, List<String> args, Exception ex) {
        super("Failed to process token. Token: "+tokenName+", data: "+Arrays.toString(args.toArray()),
            tokenName, ex);
        this.args = Collections.unmodifiableList(args);
    }

    public List<String> getTokenArgs() {
        return args;
    }

}
