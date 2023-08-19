package com.nulabinc.zxcvbn;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;

public class JSScriptEngineBuilder {

  public ScriptEngine build() {
    final GraalJSScriptEngine engine =
        GraalJSScriptEngine.create(
            Engine.newBuilder().option("engine.WarnInterpreterOnly", "false").build(),
            Context.newBuilder("js"));
    loadZxcvbnJs(engine);
    return engine;
  }

  private void loadZxcvbnJs(ScriptEngine engine) {
    try {
      // using the 4.4.1 release
      URL script = JSScriptEngineBuilder.class.getClassLoader().getResource("zxcvbn.js");
      engine.eval(new FileReader(new File(script.toURI())));
    } catch (URISyntaxException | FileNotFoundException | ScriptException e) {
      throw new RuntimeException("Cannot instantiate Javascript Engine", e);
    }
  }
}
