
# zxcvbn4j [![Build Status](https://travis-ci.org/nulab/zxcvbn4j.svg?branch=master)](https://travis-ci.org/nulab/zxcvbn4j) [![Coverage Status](https://coveralls.io/repos/nulab/zxcvbn4j/badge.svg?branch=master&service=github)](https://coveralls.io/github/nulab/zxcvbn4j?branch=master)

This is a java port of [zxcvbn](https://github.com/dropbox/zxcvbn), which is a JavaScript password strength generator. (英語の下に日本文が記載されています)

## Update

* 2015/12/24 1.0.0 released. Port of [zxcvbn 4.2.0](https://github.com/dropbox/zxcvbn/releases/tag/4.2.0)

## Special Feature

* It includes JIS keyboard layout in spatial matching.

## Install

### gradle

```
coming soon
```

### maven

```
coming soon
```

## Usage

Basic Usage. This is also available Android.

```
Zxcvbn zxcvbn = new Zxcvbn();
Strength strength = zxcvbn.measure("This is password");
```

If you want to add your own dictionary, put the keyword list of List <String> type to the second argument.

```
List<String> sanitizedInputs = new ArrayList();
sanitizedInputs.add("nulab");
sanitizedInputs.add("backlog");
sanitizedInputs.add("cacoo");
sanitizedInputs.add("typetalk");

Zxcvbn zxcvbn = new Zxcvbn();
Strength strength = zxcvbn.measure("This is password", sanitizedInputs);
```

The return result is "Strength". It's almost the same as [zxcvbn](https://github.com/dropbox/zxcvbn).

```
# estimated guesses needed to crack password
strength.guesses

# order of magnitude of strength.guesses
strength.guessesLog10

# dictionary of back-of-the-envelope crack time
# estimations, in seconds, based on a few scenarios
strength.crackTimeSeconds
{
  # online attack on a service that ratelimits password auth attempts.
  onlineThrottling100PerHour

  # online attack on a service that doesn't ratelimit,
  # or where an attacker has outsmarted ratelimiting.
  onlineNoThrottling10PerSecond

  # offline attack. assumes multiple attackers,
  # proper user-unique salting, and a slow hash function
  # w/ moderate work factor, such as bcrypt, scrypt, PBKDF2.
  offlineSlowHashing1e4PerSecond

  # offline attack with user-unique salting but a fast hash
  # function like SHA-1, SHA-256 or MD5. A wide range of
  # reasonable numbers anywhere from one billion - one trillion
  # guesses per second, depending on number of cores and machines.
  # ballparking at 10B/sec.
  offlineFastHashing1e10PerSecond
}

# same keys as result.crack_time_seconds,
# with friendlier display string values:
# "less than a second", "3 hours", "centuries", etc.
strength.crackTimeDisplay

# Integer from 0-4 (useful for implementing a strength bar)
# 0 Weak        （guesses < ^ 3 10）
# 1 Fair        （guesses <^ 6 10）
# 2 Good        （guesses <^ 8 10）
# 3 Strong      （guesses < 10 ^ 10）
# 4 Very strong （guesses >= 10 ^ 10）
strength.score

# verbal feedback to help choose better passwords. set when score <= 2.
strength.feedback
{
  # explains what's wrong, eg. 'this is a top-10 common password'.
  # not always set -- sometimes an empty string
  warning

  # a possibly-empty list of suggestions to help choose a less
  # guessable password. eg. 'Add another word or two'
  suggestions
}

# the list of patterns that zxcvbn based the guess calculation on.
strength.sequence

# how long it took zxcvbn to calculate an answer, in milliseconds.
strength.calc_time
```

## License

MIT License

* http://www.opensource.org/licenses/mit-license.php

## Requires

* Java 1.7



# zxcvbn4j とは

zxcvbn4j は、JavaScriptのパスワード強度ジェネレータである[zxcvbn]（ https://github.com/dropbox/zxcvbn ）のJava版です。

## 特別な機能

* 隣接したキー配列の照合処理にJISキーボードを対応。

## インストール

### gradle を利用する場合

```
coming soon
```

### maven を利用する場合

```
coming soon
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


