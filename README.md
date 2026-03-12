Сгенерировать тестовые CSV
```bash
./gradlew run --args="samples samples logs/app.log"
```

Запустить только cos
```bash
./gradlew run --args="cos samples/input/cos-input.csv results/cos.csv logs/cos.log"
```

Запустить только ctg
```bash
./gradlew run --args="ctg samples/input/ctg-input.csv results/ctg.csv logs/ctg.log"
```

Запустить всю систему
```bash
./gradlew run --args="system samples/input/system-input.csv results/system.csv logs/system.log"
```