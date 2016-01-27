
# zxcvbn4j [![Build Status](https://travis-ci.org/nulab/zxcvbn4j.svg?branch=master)](https://travis-ci.org/nulab/zxcvbn4j) [![Coverage Status](https://coveralls.io/repos/nulab/zxcvbn4j/badge.svg?branch=master&service=github)](https://coveralls.io/github/nulab/zxcvbn4j?branch=master)

zxcvbn4j は、JavaScriptのパスワード強度ジェネレータである[zxcvbn](https://github.com/dropbox/zxcvbn)をJavaにポーティングしたものです。

## 更新

* 2015/12/24 1.0.0 リリース. [zxcvbn 4.2.0](https://github.com/dropbox/zxcvbn/releases/tag/4.2.0)をポーティング

## 特別な機能

* 隣接したキー配列の照合処理にJISキーボードを対応。

## インストール

### gradle を利用する場合

```
'com.nulab-inc:zxcvbn:1.0.0'
```

### maven を利用する場合

```
<dependency>
  <groupId>com.nulab-inc</groupId>
  <artifactId>zxcvbn</artifactId>
  <version>1.0.0</version>
</dependency>
```

## ビルド

ビルド方法:

```
$ git clone git@github.com:nulab/zxcvbn4j.git
$ cd zxcvbn4j/
$ ./gradlew build
```

## 使い方

基本的な使い方です。Androidも同じようにご利用できます。

```
Zxcvbn zxcvbn = new Zxcvbn();
Strength strength = zxcvbn.measure("This is password");
```

独自の辞書を追加したい場合は、第二引数にリスト<文字列>のタイプのキーワード一覧を渡します。

```
List<String> sanitizedInputs = new ArrayList();
sanitizedInputs.add("nulab");
sanitizedInputs.add("backlog");
sanitizedInputs.add("cacoo");
sanitizedInputs.add("typetalk");

Zxcvbn zxcvbn = new Zxcvbn();
Strength strength = zxcvbn.measure("This is password", sanitizedInputs);
```

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
# 0 弱い       （guesses < ^ 3 10）
# 1 やや弱い    （guesses <^ 6 10）
# 2 普通       （guesses <^ 8 10）
# 3 強い       （guesses < 10 ^ 10）
# 4 とても強い   （guesses >= 10 ^ 10）
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

## バグ報告やご意見

バグ報告, ご意見、ご質問等は [Github Issues](https://github.com/nulab/zxcvbn4j/issues) にお願い致します。

## ライセンス

MIT License

* http://www.opensource.org/licenses/mit-license.php

## 要件

* Java 1.7以上

## このライブラリを使用しているアプリケーション

- [Cacoo](https://cacoo.com/)
- [Typetalk](https://typetalk.in/)
