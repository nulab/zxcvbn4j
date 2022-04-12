
# zxcvbn4j [![Build](https://github.com/nulab/zxcvbn4j/actions/workflows/build.yml/badge.svg)](https://github.com/nulab/zxcvbn4j/actions/workflows/build.yml) [![Coverage Status](https://coveralls.io/repos/nulab/zxcvbn4j/badge.svg?branch=master&service=github)](https://coveralls.io/github/nulab/zxcvbn4j?branch=master) [![Maven Central](https://img.shields.io/maven-central/v/com.nulab-inc/zxcvbn.svg)](https://img.shields.io/maven-central/v/com.nulab-inc/zxcvbn.svg)

zxcvbn4j は、JavaScriptのパスワード強度ジェネレータである[zxcvbn](https://github.com/dropbox/zxcvbn)をJavaにポーティングしたものです。

**関連記事:**

- [真のパスワード強度を測定する5つのアルゴリズム](https://nulab-inc.com/ja/blog/nulab/password-strength/)

## 目次

* [更新](#更新)
* [特別な機能](#特別な機能)
  + [内部辞書とキーボードのカスタマイズ](#内部辞書とキーボードのカスタマイズ)
  + [フィードバックメッセージのローカライズ](#フィードバックメッセージのローカライズ)
  + [デフォルトで様々な言語をサポート](#デフォルトで様々な言語をサポート)
  + [JISキーボードに対応](#JISキーボードに対応)
  + [パスワードの引数はStringだけでなくCharSequenceも受付可能](#パスワードの引数はStringだけでなくCharSequenceも受付可能)
* [インストール](#インストール)
* [開発](#開発)
* [使い方](#使い方)
  + [基本](#基本)
  + [強度の情報](#強度の情報)
* [辞書とキーボードのカスタマイズ](#辞書とキーボードのカスタマイズ)
  + [クラスパスから取得したリソースを使用する](#クラスパスから取得したリソースを使用する)
  + [HTTPを介して取得したリソースを使用する](#HTTPを介して取得したリソースを使用する)
  + [クラスパス以外のファイルのリソースを使用する](#クラスパス以外のファイルのリソースを使用する)
  + [全てのデフォルトリソースを使用する](#全てのデフォルトリソースを使用する)
  + [デフォルトリソースから選択して使用する](#デフォルトリソースから選択して使用する)
* [フィードバックメッセージのローカライズ](#フィードバックメッセージのローカライズ)
    - [リソースバンドルを指定してローカライズする](#リソースバンドルを指定してローカライズする)
    - [リソースバンドルのセットを使ってローカライズする](#リソースバンドルのセットを使ってローカライズする)
* [要件](#要件)
* [このライブラリを使用](#このライブラリを使用)
* [バグ報告やご意見](#バグ報告やご意見)
* [ライセンス](#ライセンス)

## 更新

以下のバージョンは[zxcvbn 4.4.2](https://github.com/dropbox/zxcvbn/releases/tag/v4.4.2)をポーティング

* 2022/04/05 1.6.0 リリース.
* 2021/06/08 1.5.2 リリース.
* 2021/06/05 1.5.1 リリース.
* 2021/04/26 1.5.0 リリース.
* 2021/03/22 1.4.1 リリース.
* 2021/02/19 1.4.0 リリース.
* 2021/02/09 1.3.6 リリース.
* 2021/02/02 1.3.5 リリース.
* 2021/01/26 1.3.4 リリース.
* 2021/01/19 1.3.3 リリース.
* 2021/01/19 1.3.2 リリース.
* 2020/10/28 1.3.1 リリース.
* 2019/10/19 1.3.0 リリース.
* 2019/07/23 1.2.7 リリース.
* 2019/07/16 1.2.6 リリース.
* 2018/03/30 1.2.5 リリース.
* 2018/02/27 1.2.4 リリース.
* 2017/03/27 1.2.3 リリース.

以下のバージョンは[zxcvbn 4.4.1](https://github.com/dropbox/zxcvbn/releases/tag/v4.4.1)をポーティング

* 2016/12/07 1.2.2 リリース.
* 2016/12/03 1.2.1 リリース.

以下のバージョンは[zxcvbn 4.4.0](https://github.com/dropbox/zxcvbn/releases/tag/v4.4.0)をポーティング

* 2016/10/29 1.2.0 リリース.

以下のバージョンは[zxcvbn 4.3.0](https://github.com/dropbox/zxcvbn/releases/tag/4.3.0)をポーティング

* 2016/10/01 1.1.6 リリース.
* 2016/09/27 1.1.5 リリース.
* 2016/07/08 1.1.4 リリース.
* 2016/05/27 1.1.3 リリース.
* 2016/05/25 1.1.2 リリース.
* 2016/03/19 1.1.1 リリース.
* 2016/03/06 1.1.0 リリース.

以下のバージョンは[zxcvbn 4.2.0](https://github.com/dropbox/zxcvbn/releases/tag/4.2.0)をポーティング

* 2016/01/28 1.0.2 リリース.
* 2016/01/27 1.0.1 リリース.
* 2015/12/24 1.0.0 リリース.

## 特別な機能

### 内部辞書とキーボードのカスタマイズ

* 測定アルゴリズムが使用する辞書とキーボードレイアウトをカスタマイズできます。

### フィードバックメッセージのローカライズ

* フィードバックメッセージを任意の言語にローカライズできます。

### デフォルトで様々な言語をサポート

測定結果のフィードバックメッセージに対応する言語

- English ([default](./src/main/resources/com/nulabinc/zxcvbn/messages.properties))
- Japanese ([ja](./src/main/resources/com/nulabinc/zxcvbn/messages_ja.properties))
- Dutch ([nl](./src/main/resources/com/nulabinc/zxcvbn/messages_nl.properties))
- German ([de](./src/main/resources/com/nulabinc/zxcvbn/messages_de.properties))
- French ([fr](./src/main/resources/com/nulabinc/zxcvbn/messages_fr.properties))
- Italian ([it](./src/main/resources/com/nulabinc/zxcvbn/messages_it.properties))

### JISキーボードに対応

* 隣接したキー配列の照合処理にJISキーボードを対応

### パスワードの引数はStringだけでなくCharSequenceも受付可能

* これによりパスワードのフォーマットを柔軟に変更可能。
* センシティブな中間オブジェクトにも文字列を使用しない。

## インストール

Gradle:

```
compile 'com.nulab-inc:zxcvbn:1.6.0'
```

Maven:

```
<dependency>
  <groupId>com.nulab-inc</groupId>
  <artifactId>zxcvbn</artifactId>
  <version>1.6.0</version>
</dependency>
```

## 開発

``` bash
$ git clone https://github.com/nulab/zxcvbn4j.git
$ cd ./zxcvbn4j
$ ./gradlew build    # build
$ ./gradlew test     # test
```

## 使い方

### 基本


基本的な使い方です。Androidも同じようにご利用できます。

``` java
Zxcvbn zxcvbn = new Zxcvbn();
Strength strength = zxcvbn.measure("This is password");
```

独自の辞書を追加したい場合は、第二引数にリスト<文字列>のタイプのキーワード一覧を渡します。

``` java
List<String> sanitizedInputs = new ArrayList();
sanitizedInputs.add("nulab");
sanitizedInputs.add("backlog");
sanitizedInputs.add("cacoo");
sanitizedInputs.add("typetalk");

Zxcvbn zxcvbn = new Zxcvbn();
Strength strength = zxcvbn.measure("This is password", sanitizedInputs);
```

### 強度の情報

返却する結果は、"Strength"インスタンスです。[zxcvbn](https://github.com/dropbox/zxcvbn) が返却する結果とほぼ同じものです。

```
# パスワードの「乱雑さ」「複雑さ」を表す指標
strength.guesses
strength.guessesLog10

# いくつかのシナリオに基づいたクラック時間の推測
strength.crackTimeSeconds
{
  # オンライン攻撃でパスワード認証に回数制限が有る場合
  onlineThrottling100PerHour

  # オンライン攻撃でパスワード認証に回数制限が無い場合
  onlineNoThrottling10PerSecond

  # オフライン攻撃で、bcrypt、scrypt、PBKDF2等を使ってハッシュ化している場合
  offlineSlowHashing1e4PerSecond

  # オフライン攻撃で、SHA-1、SHA-256またはMD5等を使ってハッシュ化している場合
  offlineFastHashing1e10PerSecond
}

# strength.crackTimeSecondsを表示するために文字列化した値(秒未満、3時間、世紀 等)
strength.crackTimeDisplay


# 0から4の整数
# 0 弱い      （guesses < 10^3 + 5）
# 1 やや弱い   （guesses < 10^6 + 5）
# 2 普通      （guesses < 10^8 + 5）
# 3 強い      （guesses < 10^10 + 5）
# 4 とても強い （guesses >= 10^10 + 5）
strength.score

# 安全なパスワード作成に役立つフィードバック。(score <= 2 のみ表示)
{
  # 警告文
  warning

  # 提案
  suggestions
}

# 測定に用いたパターンのリスト
strength.sequence

# 測定にかかった時間
strength.calc_time
```

## 辞書とキーボードのカスタマイズ

`ZxcvbnBuilder`を使って測定処理で使用する辞書とキーボードをカスタマイズできます。

### クラスパスから取得したリソースを使用する

クラスパス上の独自の辞書ファイルやキーボードファイルを`ClasspathResource`を使って取得できます。
辞書ファイルは`DictionaryLoader`を使ってロードします。
キーボードファイルは`SlantedKeyboardLoader`か`AlignedKeyboardLoader`を使ってロードします。

``` java
Zxcvbn zxcvbn = new ZxcvbnBuilder()
        .dictionary(new DictionaryLoader("us_tv_and_film", new ClasspathResource("/com/nulabinc/zxcvbn/matchers/dictionarys/us_tv_and_film.txt")).load())
        .keyboard(new SlantedKeyboardLoader("qwerty", new ClasspathResource("/com/nulabinc/zxcvbn/matchers/keyboards/qwerty.txt")).load())
        .keyboard(new AlignedKeyboardLoader("keypad", new ClasspathResource("/com/nulabinc/zxcvbn/matchers/keyboards/keypad.txt")).load())
        .build();
```

### HTTPを介して取得したリソースを使用する

`Resource interface`を実装するとクラスパス以外の辞書・キーボードファイルも取得できます。
以下のコードはHTTP(s)を使ってファイルを取得してロードしています。

``` java
URL dictionaryURL = new URL("https://example.com/foo/dictionary.txt");
Resource myDictionaryResource = new MyResourceOverHTTP(dictionaryURL);

URL keyboardURL = new URL("https://example.com/bar/keyboard.txt");
Resource myKeyboardURLResource = new MyResourceOverHTTP(keyboardURL);

Zxcvbn zxcvbn = new ZxcvbnBuilder()
        .dictionary(new DictionaryLoader("my_dictionary", myDictionaryResource).load())
        .keyboard(new SlantedKeyboardLoader("my_keyboard", myKeyboardURLResource).load())
        .build();

public class MyResourceOverHTTP implements Resource {

    private URL url;

    public MyResourceOverHTTP(URL url) {
        this.url = url;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) this.url.openConnection();
        return conn.getInputStream();
    }
}
```

### クラスパス以外のファイルのリソースを使用する

以下のコードはクラスパス以外の他のディレクトリのファイルを取得してロードしています。

``` java
File dictionaryFile = new File("/home/foo/dictionary.txt");
Resource myDictionaryResource = new MyResourceFromFile(dictionaryFile);

File keyboardFile = new File("/home/bar/keyboard.txt");
Resource myKeyboardURLResource = new MyResourceFromFile(keyboardFile);

Zxcvbn zxcvbn = new ZxcvbnBuilder()
    .dictionary(new DictionaryLoader("my_dictionary", myDictionaryResource).load())
    .keyboard(new SlantedKeyboardLoader("my_keyboard", myKeyboardURLResource).load())
    .build();

public class MyResourceFromFile implements Resource {

    private File file;

    public MyResourceFromFile(File file) {
        this.file = file;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }
}
```

### 全てのデフォルトリソースを使用する

StandardDictionariesを使ってデフォルトの辞書ファイルやキーボードをロードできます。
`StandardDictionaries.loadAllDictionaries()`はデフォルトの全ての辞書ファイルをロードします。
`StandardDictionaries.loadAllKeyboards()`はデフォルトの全てのキーボードファイルをロードします。

``` java
Zxcvbn zxcvbn = new Zxcvbn();
```

or

``` java
Zxcvbn zxcvbn = new ZxcvbnBuilder()
    .dictionaries(StandardDictionaries.loadAllDictionaries())
    .keyboards(StandardKeyboards.loadAllKeyboards())
    .build();
```

### デフォルトリソースから選択して使用する

デフォルトの辞書ファイルやキーボードから一部を選択してロードできます。

``` java
Zxcvbn zxcvbn = new ZxcvbnBuilder()
    .dictionary(StandardDictionaries.ENGLISH_WIKIPEDIA_LOADER.load())
    .dictionary(StandardDictionaries.PASSWORDS_LOADER.load())
    .keyboard(StandardKeyboards.QWERTY_LOADER.load())
    .keyboard(StandardKeyboards.DVORAK_LOADER.load())
    .build();
```

## フィードバックメッセージのローカライズ

zxcvbn4jは英語で返却されるフィードバックメッセージを他の言語に変更可能です。

#### リソースバンドルを指定してローカライズする

``` java
// パスワード強度を測定して Strength を取得します。
Zxcvbn zxcvbn = new Zxcvbn();
Strength strength = zxcvbn.measure("This is password");

// 事前に用意したプロパティファイル(※)の名前とロケールを指定して、ResourceBundle を取得します。
ResourceBundle resourceBundle = ResourceBundle.getBundle("This is bundle name", Locale.JAPAN);

// FeedbackにResourceBundleを渡して、ローカライズされたFeedbackを生成します。
Feedback feedback = strength.getFeedback();
Feedback localizedFeedback = feedback.withResourceBundle(resourceBundle);

// getSuggestions()、getWarning()で取得するフィードバックメッセージはローカライズ済みです。
List<String> localizedSuggestions = localizedFeedback.getSuggestions();
String localizedWarning = localizedFeedback.getWarning();
```

プロパティファイルに定義するキーとメッセージは、[messages.properties](https://github.com/nulab/zxcvbn4j/blob/master/src/main/resources/com/nulabinc/zxcvbn/messages.properties) を参考に作成してください。

* http://www.opensource.org/licenses/mit-license.php

#### リソースバンドルのセットを使ってローカライズする

``` java
Strength strength = zxcvbn.measure(password);
Feedback feedback = strength.getFeedback();

Map<Locale, ResourceBundle> messages = new HashMap<>();
messages.put(Locale.JAPANESE, ResourceBundle.getBundle("This is bundle name", Locale.JAPANESE));
messages.put(Locale.ITALIAN, ResourceBundle.getBundle("This is bundle name", Locale.ITALIAN));
Feedback replacedFeedback = feedback.replaceResourceBundle(messages);
```

## 要件

* Java 1.7以上

## このライブラリを使用

- [Backlog](https://backlog.com/)
- [Cacoo](https://cacoo.com/)
- [Typetalk](https://typetalk.com/)
- [JetBrains Hub](https://www.jetbrains.com/hub/)
- [Cryptomator](https://cryptomator.org/)
- And many Open Source Software
  - https://github.com/search?q=com.nulab-inc+zxcvbn&type=code
  - https://mvnrepository.com/artifact/com.nulab-inc/zxcvbn/usages

## バグ報告やご意見

バグ報告, ご意見、ご質問等は [Github Issues](https://github.com/nulab/zxcvbn4j/issues) にお願い致します。

## ライセンス

MIT License
