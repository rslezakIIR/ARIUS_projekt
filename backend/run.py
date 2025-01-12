from app import create_app
from app import db
from app.models.user import User
from app.models.coin import Coin
from app.models.order import Order, OrderItem
from app.models.review import Review

app = create_app()

@app.shell_context_processor
def make_shell_context():
    return {
        'db': db,
        'User': User,
        'Coin': Coin,
        'Order': Order,
        'OrderItem': OrderItem,
        'Review': Review
    }

if __name__ == '__main__':
    app.run(host='localhost', port=5000)