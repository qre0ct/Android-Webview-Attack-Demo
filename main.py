from flask import Flask, Response, render_template
from flask import request, jsonify, redirect
from flask import make_response
import os

_name_ = "AppAwesomeCSP"
template_dir = os.path.abspath('static/template')
app     = Flask(_name_, template_folder=template_dir)

@app.after_request
def apply_otherHeaders(response):
    response.headers.set('Cache-Control', "no-cache, max-age=0, must-revalidate, no-store")
    return response

@app.route("/home", methods=['GET'])
def home():
    userInput = request.args.get('name')
    if userInput:
        r = make_response(render_template('appAwesomeCheckout.html', ui=userInput))
        return r
    else:
        return app.send_static_file('appAwesomeCheckout.html')

@app.route("/checkout")
def checkout():
    return render_template('checkout.html')


if __name__ == '__main__':
  app.run(debug=True, host="192.168.1.35", port=31337)
