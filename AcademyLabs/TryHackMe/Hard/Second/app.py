from flask import Flask, render_template, request, redirect, url_for, session, render_template_string
from flask_mysqldb import MySQL
import MySQLdb.cursors
import re


app = Flask(__name__)

app.secret_key = '$uper@W3s0m3K3y!'

app.config['MYSQL_HOST'] = 'localhost'
app.config['MYSQL_USER'] = 'smokey'
app.config['MYSQL_PASSWORD'] = '$tr0nG_P@sS_W0rD@!'
app.config['MYSQL_DB'] = 'second_project'

mysql = MySQL(app)

@app.route('/')
@app.route('/login', methods =['GET', 'POST'])
def login():
        msg = ''
        blacklist = ["config","self","_",'"']
        if request.method == 'POST' and 'username' in request.form and 'password' in request.form:
                username = request.form['username']
                password = request.form['password']
                cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
                cursor.execute('SELECT * FROM users WHERE username = % s AND password = % s', (username, password, ))
                account = cursor.fetchone()
                for check in blacklist:
                    if check in username:
                        msg = "WAF test"
                        return render_template_string(msg)
                if account:
                        session['loggedin'] = True
                        session['id'] = account['id']
                        session['username'] = account['username']
                        msg = '''<!-- Store this code in 'index.html' file inside the 'templates' folder-->

<html>
        <head>
                <meta charset="UTF-8">
                <title> Index </title>
                <link rel="stylesheet" href="{{ url_for('static', filename='style.css') }}">
        </head>
        <body></br></br></br></br></br>
                <div align="center">
                <div align="center" class="border">
                        <div class="header">
                                <h1 class="word">Index</h1>
                        </div></br></br></br>
                                <h1 class="bottom">
                                        Hi %s!!</br></br> Welcome to the index page...
                                </h1></br></br></br>
                                <a href="{{ url_for('logout') }}" class="btn">Logout</a>
                </div>
                </div>
        </body>
</html>'''% session['username']
                        return render_template_string(msg)
                else:
                        msg = 'Incorrect username / password !'
        return render_template('login.html', msg = msg)

@app.route('/logout')
def logout():
        session.pop('loggedin', None)
        session.pop('id', None)
        session.pop('username', None)
        return redirect(url_for('login'))

@app.route('/register', methods =['GET', 'POST'])
def register():
        msg = ''
        if request.method == 'POST' and 'username' in request.form and 'password' in request.form and 'email' in request.form :
                username = request.form['username']
                password = request.form['password']
                email = request.form['email']
                cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
                cursor.execute('SELECT * FROM users WHERE username = % s', (username, ))
                account = cursor.fetchone()
                if account:
                        msg = 'Account already exists !'
                elif not re.match(r'[^@]+@[^@]+\.[^@]+', email):
                        msg = 'Invalid email address !'
                elif not username or not password or not email:
                        msg = 'Please fill out the form !'
                else:
                        cursor.execute('INSERT INTO users VALUES (NULL, % s, % s, % s)', (username, password, email, ))
                        mysql.connection.commit()
                        msg = 'You have successfully registered !'
        elif request.method == 'POST':
                msg = 'Please fill out the form !'
        return render_template('register.html', msg = msg)

if __name__=="__main__":
    app.run("127.0.0.1",5000)
