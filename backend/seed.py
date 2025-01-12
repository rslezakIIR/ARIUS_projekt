from app import create_app, db
from app.models.coin import Coin

# Tworzenie aplikacji
app = create_app()

# Dodawanie przykładowych danych
with app.app_context():
    db.create_all()  # Tworzy tabele w bazie danych

    # Przykładowe dane
    coins = [
        Coin(
            name="US Flag",
            description="American flag coin from 2020.",
            price=5.75,
            year_issued=2020,
            country="USA",
            quantity_available=200,
            image_url="/assets/us.jpg"
        ),
        Coin(
            name="British Queen",
            description="Coin commemorating the British Queen.",
            price=12.00,
            year_issued=2019,
            country="UK",
            quantity_available=150,
            image_url="/assets/british_queen.jpg"
        ),
        Coin(
            name="Australian Kangaroo",
            description="Coin featuring a kangaroo from Australia.",
            price=8.50,
            year_issued=2018,
            country="Australia",
            quantity_available=120,
            image_url="/assets/australian_kangaroo.jpg"
        ),
        Coin(
            name="Canadian Maple Leaf",
            description="Canadian coin featuring a maple leaf.",
            price=10.00,
            year_issued=2021,
            country="Canada",
            quantity_available=180,
            image_url="/assets/canadian_maple_leaf.jpg"
        ),
        Coin(
            name="Chinese Panda",
            description="Coin featuring a panda from China.",
            price=15.00,
            year_issued=2022,
            country="China",
            quantity_available=130,
            image_url="/assets/chinese_panda.jpg"
        ),
        Coin(
            name="Mexican Libertad",
            description="Coin featuring the Angel of Independence from Mexico.",
            price=9.00,
            year_issued=2017,
            country="Mexico",
            quantity_available=100,
            image_url="/assets/mexican_libertad.jpg"
        ),
    ]

    # Dodaj znaczniki do bazy
    db.session.add_all(coins)
    db.session.commit()

    print("Dane testowe zostały dodane do bazy.")
