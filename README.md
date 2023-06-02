# TO DO LIST:

### ~~Сформулировать идею и наметить желаемый результат~~
### ~~Создать команду и распределить роли~~
### ~~Описать проект (ТЗ)~~
### ~~Построить пошаговый план действи~~
### Нарисовать архитектуру > выполнено 65%
### ~~Собрать черновик приложения~~
### ~~Собрать простого тг бота, через которого можно будет авторизоваться/пополнить~~
### Сделать вход в приложение через логин и пароль > выполнено 10%)
### Сделать меню с выбором игры и прочего функционала > выполнено 10%)
### Сделать игры
### Привести к красивому виду (сделать дизайн)
### Написать документацию кода

***
***
***
***

# SAMOE KREATIVNOE

> Филиппов Матвей ([MatvteyFilippov](https://github.com/MatvteFilippov "GitHub"))
>> тимлид

> Рябков Александр ([realyoungwoody](https://github.com/realyoungwoody "GitHub"))
>> разработчик

> Калашникова Валерия ([ValeriaKalashnikova](https://github.com/ValeriaKalashnikova "GitHub"))
>> разработчик

> Носова Алина ([alinjia](https://github.com/alinjia "GitHub"))
>> тестировщик

> Даша Чубарова ([chubarova2003](https://github.com/chubarova2003 "GitHub"))
>> тестировщик

# BURMALDA
### Бурмалда - гамблинг разрабатывемый командой "Самое креативное"
#### `Application.java` - запускает работу приложение основываясь на FXML код (main)
#### `Controller.java` - контролирует все процессы внутри приложения (отвечает за обработку нажатий кнопок и тд)
#### `view.fxml` - создаёт образ (внешний вид) приложения, связывает кнопки и прочее с их функциями
#### `burmolda_bot.py` - бот тг, умеет создавать аккаун, пополнять баланс и пушить обновления в онлайн [базу данных](https://burmoldabot.dvervevre.repl.co "хэшированная")

***
***
***

## Общее описание (ТЗ):
В рамках данного проекта нашей команде предстоит реализовать приложение для игры в импровизированное казино.

Burmolda должна:
1. Иметь графический интерфейс (GUI)
2. Иметь онлайн базу данных
3. Иметь возможность онлайн ввода/вывода импровизированных монет
4. Иметь возможность играть в 2 и более игры
5. Использовать шифровку SHA_256 (для гаранта честности исхода)
6. Иметь элементы интерфейса, описанные ниже в режимах доступа

### Режимы доступа и их возможности:
#### Игрок
- Регистрация
- Личный кабинет
- Ввод/вывод монет
- Выбор игры (и сама игра)
- Обращение в поддержку

#### ~~Администратор~~ (не будет реализовано)
- Регистрация
- Личный кабинет
- Чат с другими игроками
- Донат
- Просмотр статистики и базы данных
- Ответ пользователям от лица поддержки

#### ~~Инвестор~~ (не будет реализовано)
- Регистрация
- Личный кабинет
- Онлайн пополнение
- Обращение в поддержку
- Пополнение банка определённой игры
###### Забрать деньги обратно он может согласно вложенным процентам
###### Например в банке было 900 монет, он инвестируют 100 монет, значит он владеет 10% от всего банка (за ним закрпеляется это право)
###### Предположим банк увеличился и сейчас он составляет 5000 монет, это значит что Инвестор может забрать 500 монет из этого банка
###### При этом мы взымаем 5% комиссии от забранных денег, то есть если Инвестор забрал 500 монет, то на его счету будет 475 монет

### Окружение:
- ОС: Windows, Linux, MacOS
- Платформа: desktop, laptop
