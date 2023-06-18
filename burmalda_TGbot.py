import asyncio
import pickle
import traceback
import urllib.request

import aiogram

from background import keep_alive, tg_key, u_kassa_key, app
from aiogram.types import InlineKeyboardMarkup, InlineKeyboardButton, ContentType
import os
from aiogram import Bot, Dispatcher, executor, types
from aiogram.contrib.fsm_storage.memory import MemoryStorage
from aiogram.dispatcher.filters.state import State, StatesGroup
from aiogram.dispatcher import FSMContext
from datetime import datetime
import hashlib


bot = Bot(token=tg_key)
dp = Dispatcher(bot, storage=MemoryStorage())

logins = {}
if not os.path.exists("ankets.pickle"):
    with open("ankets.pickle", "wb") as f:
        pickle.dump(logins, f)
else:
    with open("ankets.pickle", "rb") as f:
        logins = pickle.load(f)


class Get_num(StatesGroup):
    wait = State()


class New_log(StatesGroup):
    login = State()
    pass1 = State()
    pass2 = State()
log = {}


async def compare_pickle():
    with open("ankets.pickle", 'rb') as pickle_file_compare:
        data_time_compare = pickle.load(pickle_file_compare)

    del_akk = []
    for key in data_time_compare:
        if key not in logins:
            del_akk.append(key)
    for key in del_akk:
        del data_time_compare[key]

    for key, value in logins.items():
        if key not in data_time_compare:
            data_time_compare[key] = value
        elif data_time_compare[key] != value:
            data_time_compare[key] = value

    with open("ankets.pickle", 'wb') as pickle_file_compare:
        pickle.dump(data_time_compare, pickle_file_compare)

    urllib.request.urlopen("https://burmalda.dvervevre.repl.co")


async def error_handler(update: types.Update, exception: Exception):
    error_text = traceback.format_exception(type(exception), exception, exception.__traceback__)
    tb = traceback.extract_tb(exception.__traceback__)
    try:
        await bot.send_message(chat_id=update.message.chat.id,
                               text=f"{datetime.now()}\n\n<b>ERROR</b>\nПередал информацию об ошибку разработчику, попробуйте позже",
                               parse_mode="HTML")
        text = f"Ошибка от @{update.message.from_user.username}"
    except AttributeError:
        text = "Ошибка в самом коде:"
    except (aiogram.utils.exceptions.ChatNotFound, aiogram.utils.exceptions.BotBlocked):
        pass
    try:
        await bot.send_message(chat_id=1041950401,
                               text=f"{datetime.now()}\nПроизошла ошибка <b>{type(exception).__name__}</b> в функции {tb[-1].name}",
                               parse_mode="HTML")
        await bot.send_message(chat_id=1041950401,
                               text=text + f"\n\n<code>{error_text}</code>",
                               parse_mode="HTML")
    except (aiogram.utils.exceptions.ChatNotFound, aiogram.utils.exceptions.BotBlocked):
        pass
dp.register_errors_handler(error_handler)


@dp.message_handler(commands=['start'])
async def start(message: types.Message):
    if message.from_user.id not in logins:
        inline_kb = InlineKeyboardMarkup().add(InlineKeyboardButton("Создать аккаунт", callback_data="make_akk"))
        await message.answer(text=f"Добро пожаловать, {message.from_user.first_name}!\n<b>У меня нет вашего логина(</b>",
                             reply_markup=inline_kb, parse_mode="HTML")
    else:
        await message.answer(text=f"Привет, <b>{logins[message.from_user.id][0]}</b>!\nЧтоб пополнить или узнать баланс пришли комманду /deposit",
                             parse_mode="HTML")


async def is_in(user_id: int) -> bool:
    if user_id in logins:
        return True
    else:
        try:
            inline_kb = InlineKeyboardMarkup().add(InlineKeyboardButton("Создать аккаунт", callback_data="make_akk"))
            await bot.send_message(chat_id=user_id, text="У меня нет вашего логина(", reply_markup=inline_kb)
        except (aiogram.utils.exceptions.ChatNotFound, aiogram.utils.exceptions.BotBlocked):
            pass


@dp.message_handler(commands=['delete'])
async def delete(message: types.Message):
    if await is_in(message.from_user.id):
        kb_btn = [
            [
                InlineKeyboardButton("Да", callback_data="del_akk"),
                InlineKeyboardButton("Нет", callback_data="close+1")
            ]
        ]
        inline_kb = InlineKeyboardMarkup(inline_keyboard=kb_btn)
        await message.answer("Вы уверены что хотите <b>удалить аккаунт?</b>", parse_mode="HTML", reply_markup=inline_kb)


@dp.callback_query_handler(text="del_akk")
async def del_akk(callback: types.CallbackQuery):
    await callback.answer("Удаляю аккаунт")
    if await is_in(callback.from_user.id):
        del logins[callback.from_user.id]
        await compare_pickle()


@dp.message_handler(commands=['deposit'])
async def deposit(message: types.Message):
    if await is_in(message.from_user.id):
        kb_btn = [
            [
                InlineKeyboardButton("Пополнить", callback_data="add_money"),
                InlineKeyboardButton("Закрыть", callback_data="close+1")
            ]
        ]
        inline_kb = InlineKeyboardMarkup(inline_keyboard=kb_btn)
        await message.answer(f"Сейчас на балансе {logins[message.from_user.id][2]}₽", reply_markup=inline_kb)


async def com_st(message: types.Message, state=FSMContext):
    if message.text == "/start":
        await state.finish()
        await start(message)
        return True


@dp.callback_query_handler(text="make_akk")
async def order_1(callback: types.CallbackQuery):
    await callback.answer("Создаём аккаунт")
    await callback.message.delete()
    log[callback.from_user.id] = ["login", "pass"]
    await callback.message.answer("Придумайте логин:")
    await New_log.login.set()


@dp.message_handler(state=New_log.login)
async def order_2(message: types.Message, state=FSMContext):
    if not await com_st(message, state):
        if message.text.count(" "):
            await message.answer("Логин не может содержать пробел\nПридумайте другой:")
            return
        for val in logins.values():
            if message.text in val[0]:
                await message.answer("Такой логин уже занят!\nПридумайте другой:")
                return
        log[message.from_user.id][0] = message.text
        await message.answer("Придумайте пароль:")
        await New_log.pass1.set()


@dp.message_handler(state=New_log.pass1)
async def order_3(message: types.Message, state=FSMContext):
    if not await com_st(message, state):
        if len(message.text) > 7:
            log[message.from_user.id][1] = message.text
            await message.answer("Повторите пароль:")
            await New_log.pass2.set()
        else:
            await message.answer("Слишком кортокий пароль, попробуйте другой:")


@dp.message_handler(state=New_log.pass2)
async def order_4(message: types.Message, state=FSMContext):
    if not await com_st(message, state):
        if log[message.from_user.id][1] == message.text:
            await state.finish()
            hash_object = hashlib.sha256(message.text.encode())
            logins[message.from_user.id] = [log[message.from_user.id][0], hash_object.hexdigest(), 500]
            await compare_pickle()
            await message.answer("<b>Ваш аккаунт сохранён!</b>\nВ качестве бонуса вам начислено 500₽", parse_mode="HTML")
        else:
            await message.answer("Пароли отличаются, попробуйте заново:")
            await New_log.pass1.set()


@dp.callback_query_handler(text="add_money")
async def add_money(callback: types.CallbackQuery):
    await callback.answer("Пополнить баланс")
    await callback.message.answer("Введите сумму для пополнения (минимум 100 максимум 1000):")
    await Get_num.wait.set()


add_to_bal = {}
@dp.message_handler(state=Get_num.wait)
async def make_order(message: types.Message, state=FSMContext):
    await state.finish()
    if message.text == "/start":
        await start(message)
    else:
        try:
            if 100 <= float(message.text) <= 1000:
                add_to_bal[message.from_user.id] = int(message.text)
                inline_kb = InlineKeyboardMarkup().add(
                    InlineKeyboardButton("Оплатить", pay=True)).add(
                    InlineKeyboardButton("Закрыть❌", callback_data="close_pay"))
                await bot.send_invoice(
                    chat_id=message.from_user.id, title="Пополнение баланса",
                    description="Ты сможешь использовать эти деньги внутри нашего бота",
                    payload=str(message.from_user.id), provider_token=u_kassa_key, currency="RUB",
                    prices=[{"label": "Руб", "amount": int(f"{add_to_bal[message.from_user.id]}00")}], reply_markup=inline_kb)
                await message.answer("<b>!!!Пополняйте строго с этой карты!!!</b>\n\n<code>1111 1111 1111 1026</code>, <code>12/22</code>, CVC <code>000</code>",
                                     parse_mode="HTML")
            else:
                int("error")
        except ValueError:
            await message.answer("<b>Ошибка!</b>\nВведите сумму для пополнения (минимум 100 максимум 1000):",
                                 parse_mode="HTML")
            await Get_num.wait.set()


@dp.pre_checkout_query_handler()
async def process_pre_checkout_query(pre_checkout_query: types.PreCheckoutQuery):
    await bot.answer_pre_checkout_query(pre_checkout_query.id, ok=True)


@dp.message_handler(content_types=ContentType.SUCCESSFUL_PAYMENT)
async def process_pay(message: types.Message):
    if message.successful_payment.invoice_payload == str(message.from_user.id):
        logins[message.from_user.id][2] += add_to_bal[message.from_user.id]
        await compare_pickle()
        await message.answer(f"<b>Успешно пополнено!</b>\nВаш баланс: {logins[message.from_user.id][2]}",
                             parse_mode="HTML")


@app.route('/')
def home():
    try:
        with open("ankets.pickle", "rb") as f:
            dic = pickle.load(f)
            mas = []
            for val in dic.values():
                mas.append(f">>>{val[0]} :: {val[1]} ;; {val[2]}<<<")
        return mas
    except Exception:
        return "ERROR 404"


@app.route('/update_balance/<login>/<float:new_balance>')
def upd_balance(login, new_balance):
    for key, val in logins.items():
        if val[0] == login:
            try:
                logins[key][2] = float(new_balance)
                asyncio.run(compare_pickle())
                return f"{logins[key][2]}"
            except ValueError:
                return "ERROR 404"
    return "ERROR 404"


@app.route('/gell_word/<level>/<theme>')
def words_gell(level, theme):
    try:
        with open(f"burmalda_words_gell/{level}/{theme}.txt", "r") as file:
            return file.read()
    except Exception:
        return "ERROR 404"


@dp.callback_query_handler(text="close+1")
async def close_mes(callback: types.CallbackQuery):
    await callback.answer("Закрыл")
    try:
        await callback.message.delete()
        await bot.delete_message(chat_id=callback.message.chat.id, message_id=callback.message.message_id - 1)
    except aiogram.utils.exceptions.MessageToDeleteNotFound:
        pass


@dp.message_handler(content_types=types.ContentType.ANY)
async def another_message(message: types.Message):
    inline_kb = InlineKeyboardMarkup().add(
        InlineKeyboardButton("Удалить", callback_data="close+1")
    )
    await message.reply("Я вас не понял(", reply_markup=inline_kb)


keep_alive()
if __name__ == "__main__":
    executor.start_polling(dp, skip_updates=True)
