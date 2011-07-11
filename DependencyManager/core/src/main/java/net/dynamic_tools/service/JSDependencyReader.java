package net.dynamic_tools.service;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/9/11
 * Time: 7:39 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class JSDependencyReader {
    Pattern pattern;
    CharsetDecoder charsetDecoder = Charset.forName("UTF-8").newDecoder();

    public Set<String> readDependencies(File file) throws IOException, CharacterCodingException {
        Set<String> dependencySet = new HashSet<String>();
        CharBuffer charBuffer = readFileToCharBuffer(file);

        // Run some matches
        Matcher matcher = pattern.matcher(charBuffer);
        while (matcher.find()) {
            dependencySet.add(matcher.group(1));
        }

        return dependencySet;
    }

    private CharBuffer readFileToCharBuffer(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel fileChannel = fileInputStream.getChannel();

        // Get a CharBuffer from the source file
        ByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, (int) fileChannel.size());
        return charsetDecoder.decode(byteBuffer);
    }



	public void setPattern(String patternString) {
		this.pattern = Pattern.compile(patternString, Pattern.MULTILINE);
	}
}
