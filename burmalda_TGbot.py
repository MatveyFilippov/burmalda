import pickle
from background import keep_alive, tg_key
from aiogram.types import InlineKeyboardMarkup, InlineKeyboardButton, ContentType
import os
from aiogram import Bot, Dispatcher, executor, types
from aiogram.contrib.fsm_storage.memory import MemoryStorage
from aiogram.dispatcher.filters.state import State, StatesGroup
from aiogram.dispatcher import FSMContext
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

    for key, value in logins.items():
        if key not in data_time_compare:
            data_time_compare[key] = value
        elif data_time_compare[key] != value:
            data_time_compare[key] = value

    with open("ankets.pickle", 'wb') as pickle_file_compare:
        pickle.dump(data_time_compare, pickle_file_compare)


@dp.message_handler(commands=['start'])
async def start(message: types.Message):
    if message.from_user.id not in logins:
        inline_kb = InlineKeyboardMarkup().add(
            InlineKeyboardButton("Создать аккаунт", callback_data="make_akk")
        )
        text = "\nУ меня нет вашего логина("
    else:
        inline_kb = InlineKeyboardMarkup().add(
            InlineKeyboardButton("Пополнить", callback_data="add_money")
        )
        text = "\nЖелаешь пополнить баланс?"
    await message.answer(text=f"Добро пожаловать, {message.from_user.first_name}!{text}", reply_markup=inline_kb)


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
            inline_kb = InlineKeyboardMarkup().add(
                InlineKeyboardButton("Пополнить баланс", callback_data="add_money")
            )
            await message.answer("Ваш аккаунт сохранён\nВ качестве бонуса вам начислено 500₽", reply_markup=inline_kb)
        else:
            await message.answer("Пароли отличаются, попробуйте заново:")


@dp.callback_query_handler(text="add_money")
async def add_money(callback: types.CallbackQuery):
    await callback.message.answer("Введите сумму для пополнения (минимум 100):")
    await Get_num.wait.set()


add_to_bal = {}
@dp.message_handler(state=Get_num.wait)
async def make_order(message: types.Message, state=FSMContext):
    await state.finish()
    if message.text == "/start":
        await start(message)
    else:
        try:
            if int(message.text) > 99:
                add_to_bal[message.from_user.id] = int(message.text)
                inline_kb = InlineKeyboardMarkup().add(
                    InlineKeyboardButton("Оплатить", pay=True)).add(
                    InlineKeyboardButton("Закрыть❌", callback_data="close_pay"))
                await bot.send_invoice(
                    chat_id=message.from_user.id, title="Пополнение баланса",
                    description="Ты сможешь использовать эти деньги внутри нашего бота",
                    payload=str(message.from_user.id), provider_token="381764678:TEST:58464", currency="RUB",
                    prices=[{"label": "Руб", "amount": int(f"{add_to_bal[message.from_user.id]}00")}], reply_markup=inline_kb)
                await message.answer("<b>!!!Пополняйте строго с этой карты!!!</b>\n\n<code>1111 1111 1111 1026</code>, <code>12/22</code>, CVC <code>000</code>",
                                     parse_mode="HTML")
            else:
                int("error")
        except ValueError:
            await message.answer("Ошибка!\nВведите сумму для пополнения (минимум 100):")
            await Get_num.wait.set()


@dp.pre_checkout_query_handler()
async def process_pre_checkout_query(pre_checkout_query: types.PreCheckoutQuery):
    await bot.answer_pre_checkout_query(pre_checkout_query.id, ok=True)


@dp.message_handler(content_types=ContentType.SUCCESSFUL_PAYMENT)
async def process_pay(message: types.Message):
    if message.successful_payment.invoice_payload == str(message.from_user.id):
        logins[message.from_user.id][2] += add_to_bal[message.from_user.id]
        await compare_pickle()
        await message.answer(f"Успешно пополнен!\nВаш баланс: {logins[message.from_user.id][2]}")


keep_alive() # Пушит бд в интернет
if __name__ == "__main__":
    executor.start_polling(dp, skip_updates=True)
