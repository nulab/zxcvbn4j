# Changelog

## [1.4.1](https://github.com/nulab/zxcvbn4j/compare/1.4.0...1.4.1) (2021-03-20)

* Fix message from full-width to half-width [#91](https://github.com/nulab/zxcvbn4j/pull/91) ([kxn4t](https://github.com/kxn4t))

## [1.4.0](https://github.com/nulab/zxcvbn4j/compare/1.3.6...1.4.0) (2021-02-18)

* Additional french translations [#89](https://github.com/nulab/zxcvbn4j/pull/89) ([er1c](https://github.com/er1c) and [vvatanabe](https://github.com/vvatanabe))
* added german translation [#88](https://github.com/nulab/zxcvbn4j/pull/88) ([echox](https://github.com/echox))

## [1.3.6](https://github.com/nulab/zxcvbn4j/compare/1.3.5...1.3.6) (2021-02-09)

* fix NumberFormatException in DateMatcher (#78) [#87](https://github.com/nulab/zxcvbn4j/pull/87) ([vvatanabe](https://github.com/vvatanabe))

## [1.3.5](https://github.com/nulab/zxcvbn4j/compare/1.3.4...1.3.5) (2021-02-02)

* Improve L33tMatcher performance a bit [#86](https://github.com/nulab/zxcvbn4j/pull/86) ([yasuyuki-baba](https://github.com/yasuyuki-baba))

## [1.3.4](https://github.com/nulab/zxcvbn4j/compare/1.3.3...1.3.4) (2021-01-26)

* Fix Error in method Feedback.getFeedback (#82) [#83](https://github.com/nulab/zxcvbn4j/pull/83) ([mrFloony](https://github.com/mrFloony))

## [1.3.3](https://github.com/nulab/zxcvbn4j/compare/1.3.2...1.3.3) (2021-01-22)

* fallback in the process of getting the resource as a stream (#79) [#81](https://github.com/nulab/zxcvbn4j/pull/81) ([vvatanabe](https://github.com/vvatanabe))

## [1.3.2](https://github.com/nulab/zxcvbn4j/compare/1.3.1...1.3.2) (2021-01-19)

* Fix a ExceptionInInitializerError in Keycloak: error found in ResourceLoader #79 [#80](https://github.com/nulab/zxcvbn4j/pull/80) ([vvatanabe](https://github.com/vvatanabe))
* Fixed Maven Central badge link [#76](https://github.com/nulab/zxcvbn4j/pull/76) ([GlenKPeterson](https://github.com/GlenKPeterson))
* fix inconsistent score between zxcvbn4j and zxcvbn (#50) [#72](https://github.com/nulab/zxcvbn4j/pull/72) ([vvatanabe](https://github.com/vvatanabe))

## [1.3.1](https://github.com/nulab/zxcvbn4j/compare/1.3.0...1.3.1) (2020-10-28)

* Added Cryptomator to the list of applications using this library [#68](https://github.com/nulab/zxcvbn4j/pull/68) ([overheadhunter](https://github.com/overheadhunter))

## [1.3.0](https://github.com/nulab/zxcvbn4j/compare/1.2.7...1.3.0) (2019-10-19)

* Use CharSequence not String as input type for Zxcvbn.measure [#64](https://github.com/nulab/zxcvbn4j/pull/64) ([SteveLeach-Keytree](https://github.com/SteveLeach-Keytree))

## [1.2.7](https://github.com/nulab/zxcvbn4j/compare/1.2.6...1.2.7) (2019-07-23)

* remove java.nio.charset.StandardCharsets (#62) [#63](https://github.com/nulab/zxcvbn4j/pull/63) ([vvatanabe](https://github.com/vvatanabe))
* Fix resource loading when using from the web app. [#59](https://github.com/nulab/zxcvbn4j/pull/59) ([yasuyuki-baba](https://github.com/yasuyuki-baba))
* fix Error installing oraclejdk8 (#60) [#61](https://github.com/nulab/zxcvbn4j/pull/61) ([vvatanabe](https://github.com/vvatanabe))

## [1.2.6](https://github.com/nulab/zxcvbn4j/compare/1.2.5...1.2.6) (2019-07-16)

* Use system class loader to access resources [#56](https://github.com/nulab/zxcvbn4j/pull/56) ([simonschiller](https://github.com/simonschiller))
* remove openjdk7 from .travis.yml [#57](https://github.com/nulab/zxcvbn4j/pull/57) ([tsuyoshizawa](https://github.com/tsuyoshizawa))

## [1.2.5](https://github.com/nulab/zxcvbn4j/compare/1.2.4...1.2.5) (2018-03-30)

* Fixed #45 that ResourceBundle.Control is not supported in named modules [#46](https://github.com/nulab/zxcvbn4j/pull/46) ([kasecato](https://github.com/kasecato))

## [1.2.4](https://github.com/nulab/zxcvbn4j/compare/1.2.3...1.2.4) (2018-02-27)

*  Improve extensibility of Zxcvbn and Matching and optimize memory usage [#44](https://github.com/nulab/zxcvbn4j/pull/44) ([psvo](https://github.com/psvo))
* Fix the InvocationTargetException when running tests with Gradle [#42](https://github.com/nulab/zxcvbn4j/pull/42) ([sainaen](https://github.com/sainaen))
* Switch from OracleJDK7 to OpenJDK7 on Travis-CI [#41](https://github.com/nulab/zxcvbn4j/pull/41) ([sainaen](https://github.com/sainaen))
* Update README.md [#39](https://github.com/nulab/zxcvbn4j/pull/39) ([SithLordDarthVader](https://github.com/SithLordDarthVader))

## [1.2.3](https://github.com/nulab/zxcvbn4j/compare/1.2.2...1.2.3) (2017-03-27)

* port zxcvbn v4.4.2 [#36](https://github.com/nulab/zxcvbn4j/pull/36) ([vvatanabe](https://github.com/vvatanabe))
* fix inconsistent keyboard pattern [#35](https://github.com/nulab/zxcvbn4j/pull/35) ([vvatanabe](https://github.com/vvatanabe))

## [1.2.2](https://github.com/nulab/zxcvbn4j/compare/1.2.1...1.2.2) (2016-12-07)

* fixed result of spatial guess [#30](https://github.com/nulab/zxcvbn4j/pull/30) ([vvatanabe](https://github.com/vvatanabe))

## [1.2.1](https://github.com/nulab/zxcvbn4j/compare/1.2.0...1.2.1) (2016-12-03)

* port zxcvbn 4.4.1 [#28](https://github.com/nulab/zxcvbn4j/pull/28) ([vvatanabe](https://github.com/vvatanabe))
* fixed the repeat match's result is not accurate [#27](https://github.com/nulab/zxcvbn4j/pull/27) ([vvatanabe](https://github.com/vvatanabe))

## [1.2.0](https://github.com/nulab/zxcvbn4j/compare/1.1.6...1.2.0) (2016-10-29)

* port zxcvbn v4.4.0 [#25](https://github.com/nulab/zxcvbn4j/pull/25) ([vvatanabe](https://github.com/vvatanabe))

## [1.1.6](https://github.com/nulab/zxcvbn4j/compare/1.1.5...1.1.6) (2016-10-01)

* fixed ResourceBundle can not be resolved for Android [#22](https://github.com/nulab/zxcvbn4j/pull/22) ([vvatanabe](https://github.com/vvatanabe))

## [1.1.5](https://github.com/nulab/zxcvbn4j/compare/1.1.4...1.1.5) (2016-09-27)

* add check infinite [#20](https://github.com/nulab/zxcvbn4j/pull/20) ([vvatanabe](https://github.com/vvatanabe))

## [1.1.4](https://github.com/nulab/zxcvbn4j/compare/1.1.3...1.1.4) (2016-07-08)

* Add Dutch (NL) translations. [#18](https://github.com/nulab/zxcvbn4j/pull/18) ([vanDonselaar](https://github.com/vanDonselaar))

## [1.1.3](https://github.com/nulab/zxcvbn4j/compare/1.1.2...1.1.3) (2016-05-27)

* Add English message resource [#17](https://github.com/nulab/zxcvbn4j/pull/17) ([yasuyuki-baba](https://github.com/yasuyuki-baba))

## [1.1.2](https://github.com/nulab/zxcvbn4j/compare/1.1.1...1.1.2) (2016-05-25)

* i18n [#16](https://github.com/nulab/zxcvbn4j/pull/16) ([yasuyuki-baba](https://github.com/yasuyuki-baba))
* Added compile method [#15](https://github.com/nulab/zxcvbn4j/pull/15) ([PaulWoitaschek](https://github.com/PaulWoitaschek))

## [1.1.1](https://github.com/nulab/zxcvbn4j/compare/1.1.0...1.1.1) (2016-03-19)

* Refactor dictionary generation [#13](https://github.com/nulab/zxcvbn4j/pull/13) ([vvatanabe](https://github.com/vvatanabe))
* read keyboard layout from text file [#12](https://github.com/nulab/zxcvbn4j/pull/12) ([yasuyuki-baba](https://github.com/yasuyuki-baba))

## [1.1.0](https://github.com/nulab/zxcvbn4j/compare/1.0.2...1.1.0) (2016-03-06)

* #9 Port zxcvbn 4.3.0 [#11](https://github.com/nulab/zxcvbn4j/pull/11) ([vvatanabe](https://github.com/vvatanabe))
* Added JetBrains Hub to the list of applications using this library [#7](https://github.com/nulab/zxcvbn4j/pull/7) ([mazine](https://github.com/mazine))

## [1.0.2](https://github.com/nulab/zxcvbn4j/compare/1.0.1...1.0.2) (2016-01-28)

* Fix #2 [#6](https://github.com/nulab/zxcvbn4j/pull/6) ([mazine](https://github.com/mazine))

## [1.0.1](https://github.com/nulab/zxcvbn4j/compare/1.0.0...1.0.1) (2016-01-27)

* Add application using this library [#5](https://github.com/nulab/zxcvbn4j/pull/5) ([agata](https://github.com/agata))
* Customisable localisation of Feedback [#3](https://github.com/nulab/zxcvbn4j/pull/3) ([mazine](https://github.com/mazine))
* Messages punctuation [#1](https://github.com/nulab/zxcvbn4j/pull/1) ([mazine](https://github.com/mazine))

## [1.0.0](https://github.com/nulab/zxcvbn4j/compare/0cc5027ce09c...1.0.0) (2015-12-29)
