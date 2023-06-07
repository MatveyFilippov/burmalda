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
            await message.answer("Ваш аккаунт сохранён\nВ качестве бонуса вам начислено 500
