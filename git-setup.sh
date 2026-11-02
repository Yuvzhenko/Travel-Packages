#!/bin/sh
# Створює локальний репозиторій з логічною історією комітів.
# Використовує ваші git user.name / user.email. Після виконання скрипт можна видалити.
set -e
git init -b main

git add pom.xml .gitignore
git commit -m "chore: add Maven project skeleton and .gitignore"

git add data/Travel.xml data/Travel.xsd
git commit -m "feat: add tourist vouchers XML and XSD schema"

git add src/main/java/travel/model src/main/java/travel/util
git commit -m "feat(model): add domain records, enums and voucher comparators"

git add src/main/java/travel/parser/VoucherEventHandler.java \
        src/main/java/travel/parser/VoucherAssembler.java \
        src/main/java/travel/parser/VoucherParser.java \
        src/main/java/travel/parser/VoucherParseException.java \
        src/main/java/travel/parser/AbstractVoucherParser.java
git commit -m "feat(parser): add shared event handler, assembler and parser base class"

git add src/main/java/travel/parser/SaxVoucherParser.java \
        src/main/java/travel/parser/DomVoucherParser.java \
        src/main/java/travel/parser/StaxVoucherParser.java
git commit -m "feat(parser): add SAX, DOM and StAX parsers"

git add src/main/java/travel/validation
git commit -m "feat(validation): validate XML against XSD"

git add data/group-by-type.xsl src/main/java/travel/transform
git commit -m "feat(transform): group vouchers by type with XSL"

git add src/main/java/travel/Main.java src/main/resources/logging.properties
git commit -m "feat: add Main entry point and logger configuration"

git add src/test
git commit -m "test: add unit tests for model, parsers, validation and transformation"

echo "Готово. Далі: git remote add origin <URL> && git push -u origin main"
