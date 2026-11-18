package demo.security.util;

import org.apache.commons.io.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.util.regex.Pattern;

public class Utils {

    // Files passed to deleteFile() are resolved against this directory and must
    // stay within it; this prevents "../" sequences from deleting files outside
    // of the intended location.
    private static final File TARGET_DIRECTORY = new File(System.getProperty("java.io.tmpdir")).getAbsoluteFile();

    // Only allow simple arithmetic expressions to be evaluated: digits, basic
    // operators, parentheses and whitespace. This prevents arbitrary
    // JavaScript (and therefore arbitrary code) from being injected and
    // executed via untrusted input.
    private static final Pattern SAFE_SCRIPT_PATTERN = Pattern.compile("^[0-9+\\-*/().\\s]*$");

    public static KeyPair generateKey() {
        KeyPairGenerator keyPairGen;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(512);
            return keyPairGen.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static void deleteFile(String fileName) throws IOException {
        File file = new File(TARGET_DIRECTORY, fileName);
        String canonicalDestinationPath = file.getCanonicalPath();
        String canonicalTargetPath = TARGET_DIRECTORY.getCanonicalPath() + File.separator;
        if (!canonicalDestinationPath.startsWith(canonicalTargetPath)) {
            throw new IOException("Entry is outside of the target directory");
        }
        FileUtils.forceDelete(file);
    }

    public static void executeJs(String input) throws ScriptException {
        if (input == null || !SAFE_SCRIPT_PATTERN.matcher(input).matches()) {
            throw new IllegalArgumentException("Invalid script input");
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.eval(input);
    }

    public static void encrypt(byte[] key, byte[] ptxt) throws Exception {
        byte[] nonce = "7cVgr5cbdCZV".getBytes("UTF-8");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec); // Noncompliant
    }
}
