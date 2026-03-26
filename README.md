# Тригонометрическая математическая система: E2E и Fuzz-тестирование

## Описание проекта

Проект реализует систему математических функций (тригонометрических и логарифмических) на основе рядов Тейлора и обеспечивает их комплексное тестирование: модульное, интеграционное и fuzz-тестирование.

Математическая система определяется как кусочная функция:

```
f(x) = trig_branch(x),   если x <= 0
f(x) = log_branch(x),    если x > 0
```

**Тригонометрическая ветвь** (x <= 0):
```
core = ((cot(x)^2 / tan(x)) - tan(x)) * (cot(x) - sec(x)) * tan(x)
result = (core^3)^2
```

**Логарифмическая ветвь** (x > 0):
```
left = ((log10(x)^3 / log5(x)) * log3(x)) / (log2(x) / log2(x))
right = ((log2(x) - log2(x)) * log2(x)) + (log3(x) / log10(x))
result = left * right
```

## Быстрый старт

```bash
# Сгенерировать тестовые CSV
./gradlew run --args="samples samples logs/app.log"

# Запустить только cos
./gradlew run --args="cos samples/input/cos-input.csv results/cos.csv logs/cos.log"

# Запустить только ctg
./gradlew run --args="ctg samples/input/ctg-input.csv results/ctg.csv logs/ctg.log"

# Запустить всю систему
./gradlew run --args="system samples/input/system-input.csv results/system.csv logs/system.log"
```

---

## Fuzz-тестирование
**Fuzz-тестирование (фаззинг)** — это техника автоматизированного тестирования программного обеспечения, при которой на вход программы подаются случайные, неожиданные или некорректные данные с целью обнаружения ошибок, которые не выявляются при стандартном тестировании.
В данном проекте используется фаззинг на базе инструмента **Jazzer**.


## Инструмент: Jazzer
**Jazzer** — это инструмент для фаззинга JVM-приложений.


**Подключение в проекте** (build.gradle):
```groovy
dependencies {
    testImplementation 'com.code-intelligence:jazzer-junit:0.22.1'
}
```

---
## Реализация Fuzz-тестов в проекте

### Архитектура тестового набора

Fuzz-тесты размещены в пакете `org.savadanko.math.fuzz` и организованы по тестируемым компонентам:
```
src/test/java/org/savadanko/math/fuzz/
    SinCosFuzzTest.java
    LnFunctionFuzzTest.java 
    TrigCompositeFuzzTest.java
    LogFunctionFuzzTest.java
    SystemFunctionFuzzTest.java
    MathUtilsFuzzTest.java
```

Каждый fuzz-тест следует принципу **проверки инварианта**: вместо сравнения с конкретным ожидаемым значением проверяется математическое свойство, которое должно выполняться **для любого** входного значения.

### SinCosFuzzTest — базовые тригонометрические функции

**Класс:** `SinCosFuzzTest` (8 тестов)

**Проверяемые инварианты:**

| Тест | Свойство | Формула |
|------|----------|---------|
| `sinNeverThrowsAndStaysInRange` | Диапазон значений | sin(x) in [-1, 1] для конечных x |
| `cosNeverThrowsAndStaysInRange` | Диапазон значений | cos(x) in [-1, 1] для конечных x |
| `sinIsOddFunction` | Нечётность | sin(-x) = -sin(x) |
| `cosIsEvenFunction` | Чётность | cos(-x) = cos(x) |
| `pythagoreanIdentity` | Теорема Пифагора | sin^2(x) + cos^2(x) = 1 |
| `sinPeriodicity` | Периодичность | sin(x + 2pi) = sin(x) |
| `sinMatchesReference` | Эталонное сравнение | sin(x) ~= Math.sin(x) |
| `cosMatchesReference` | Эталонное сравнение | cos(x) ~= Math.cos(x) |

**Пример кода:**

```java
@FuzzTest(maxDuration = "30s")
void pythagoreanIdentity(double x) {
    if (Double.isNaN(x) || Double.isInfinite(x)) return;

    double sinX = sin.apply(x);
    double cosX = cos.apply(x);
    double sum = sinX * sinX + cosX * cosX;

    assertEquals(1.0, sum, TOLERANCE,
            "sin^2(" + x + ") + cos^2(" + x + ") = " + sum + ", expected 1.0");
}
```

### LnFunctionFuzzTest — натуральный логарифм

**Класс:** `LnFunctionFuzzTest` (5 тестов)

**Проверяемые инварианты:**

| Тест | Свойство |
|------|----------|
| `lnNeverThrows` | Корректная обработка всех специальных значений: NaN, отрицательные, 0, +Infinity |
| `lnMonotonicity` | Строгая монотонность: a < b => ln(a) < ln(b) для положительных a, b |
| `lnProductProperty` | Свойство логарифма произведения: ln(a*b) = ln(a) + ln(b) |
| `lnMatchesReference` | Совпадение с эталоном `Math.log(x)` |
| `lnSignProperty` | Знак результата: ln(x) < 0 при x < 1, ln(x) > 0 при x > 1, ln(1) = 0 |

**Пример: проверка свойства произведения**

```java
@FuzzTest(maxDuration = "30s")
void lnProductProperty(double a, double b) {
    if (Double.isNaN(a) || Double.isNaN(b)) return;
    if (Double.isInfinite(a) || Double.isInfinite(b)) return;
    if (a <= 0 || b <= 0) return;

    double product = a * b;
    if (Double.isInfinite(product) || product <= 0) return;

    double lnProduct = ln.apply(product);
    double lnSum = ln.apply(a) + ln.apply(b);

    double delta = Math.max(TOLERANCE, Math.abs(lnProduct) * 1e-5);
    assertEquals(lnProduct, lnSum, delta,
            "ln(" + a + " * " + b + ") != ln(" + a + ") + ln(" + b + ")");
}
```

### TrigCompositeFuzzTest — составные тригонометрические функции

**Класс:** `TrigCompositeFuzzTest` (5 тестов)

Составные функции (`TanFunction`, `CotFunction`, `SecFunction`) строятся через композицию базовых. Fuzz-тесты проверяют, что композиция не нарушает математические тождества.

**Проверяемые инварианты:**

| Тест | Тождество |
|------|-----------|
| `tanEqualsSinOverCos` | tan(x) = sin(x) / cos(x) |
| `cotEqualsOneOverTan` | cot(x) = 1 / tan(x) |
| `secEqualsOneOverCos` | sec(x) = 1 / cos(x) |
| `tanMatchesReference` | tan(x) ~= Math.tan(x) |
| `tanSquaredPlusOneEqualsSecSquared` | tan^2(x) + 1 = sec^2(x) |

### LogFunctionFuzzTest — логарифмы по произвольному основанию

**Класс:** `LogFunctionFuzzTest` (5 тестов)

**Проверяемые инварианты:**

| Тест | Свойство |
|------|----------|
| `log2MatchesReference` | log2(x) ~= Math.log(x) / Math.log(2) |
| `log10MatchesReference` | log10(x) ~= Math.log10(x) |
| `changeOfBaseConsistency` | log2(x) / log3(x) = const |
| `logNegativeReturnsNaN` | log2(x) = NaN для x < 0 |
| `logMonotonicity` | Монотонность: a < b => log(a) < log(b) |

### SystemFunctionFuzzTest — системная функция

**Класс:** `SystemFunctionFuzzTest` (4 теста)

**Проверяемые инварианты:**

| Тест | Свойство |
|------|----------|
| `systemNeverThrows` | Функция не выбрасывает исключений ни при каком входе |
| `systemMatchesReferenceTrigBranch` | Тригонометрическая ветвь совпадает с эталонной реализацией |
| `systemMatchesReferenceLogBranch` | Логарифмическая ветвь совпадает с эталонной реализацией |

**Пример: отсутствие исключений**

```java
@FuzzTest(maxDuration = "30s")
void systemNeverThrows(double x) {
    double result = system.apply(x);
    assertFalse(Double.isInfinite(result) && !Double.isInfinite(x),
            "system(" + x + ") returned Infinity unexpectedly");
}
```

### MathUtilsFuzzTest — вспомогательные утилиты

**Класс:** `MathUtilsFuzzTest` (5 тестов)

**Проверяемые инварианты:**

| Тест | Свойство |
|------|----------|
| `normalizeAngleAlwaysInRange` | normalizeAngle(x) in [-pi, pi] |
| `normalizeAnglePreservesTrigValues` | sin(x) = sin(normalize(x)), cos(x) = cos(normalize(x)) |
| `safeDivNeverThrows` | safeDiv никогда не выбрасывает исключений |
| `powIntConsistentWithMathPow` | powInt(base, exp) ~= Math.pow(base, exp) |
| `isZeroSymmetric` | isZero(x) = isZero(-x) |

---
## Запуск тестов

### Все тесты (включая E2E, интеграционные и fuzz)

```bash
./gradlew test
```

### Только fuzz-тесты

```bash
./gradlew test --tests "org.savadanko.math.fuzz.*"
```

### Конкретный класс fuzz-тестов

```bash
./gradlew test --tests "org.savadanko.math.fuzz.SinCosFuzzTest"
```

### Конкретный тест

```bash
./gradlew test --tests "org.savadanko.math.fuzz.SinCosFuzzTest.pythagoreanIdentity"
```

---

## Результаты
Все 32 fuzz-теста успешно проходят