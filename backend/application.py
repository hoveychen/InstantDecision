from flask import Flask
from flask.ext.sqlalchemy import SQLAlchemy
from flask.ext.admin import Admin
from flask.ext.admin.contrib.sqla import ModelView
from flask import jsonify
from flask import request
import datetime
from datetime import date
from distutils.util import strtobool

from collections import defaultdict

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:////tmp/test.db'
app.secret_key = 'zzb'
db = SQLAlchemy(app)
admin = Admin(app)

class Survey(db.Model):
  __public__ = ('id', 'name')
  id = db.Column(db.Integer, primary_key=True)
  owner = db.Column(db.String(100))
  name = db.Column(db.String(500))
  active = db.Column(db.Boolean)
  multi_selection = db.Column(db.Boolean)
  num_tickets = db.Column(db.Integer)

  def to_poj(self):
    return {'id': self.id, 'owner': self.owner, 'name': self.name, 'active': self.active,
        'multi_selection': self.multi_selection, 'num_tickets': self.num_tickets}

class Option(db.Model):
  id = db.Column(db.Integer, primary_key=True)
  survey_id = db.Column(db.Integer)
  content = db.Column(db.String(500))

  def to_poj(self):
    return {'id': self.id, 'content': self.content}

class Selection(db.Model):
  id = db.Column(db.Integer, primary_key=True)
  owner = db.Column(db.String(100))
  option_id = db.Column(db.Integer)
  survey_id = db.Column(db.Integer)

  def to_poj(self):
    return {'id': self.id, 'owner': self.owner}

@app.route('/')
def hello():
    return 'Hello World!'

@app.route('/survey/new', defaults={'survey_id': None})
@app.route('/survey/edit/<int:survey_id>')
def EditSurvey(survey_id):
  name = request.args.get('name', '')
  owner = request.args.get('owner', '')
  options = request.args.getlist('option')
  active = request.args.get('active', '')
  multi = request.args.get('multi_selection', '')
  num_tickets = request.args.get('num_tickets', '')
  if not (name and owner and options and active and multi and num_tickets):
    return jsonify({'error': 'Bad arguments.'})
  active = strtobool(active)
  multi = strtobool(multi)

  if survey_id is None:
    survey = Survey()
  else:
    survey = Survey.query.filter_by(id=survey_id).first()
    if not survey:
      return jsonify({'error': 'Id not exists.'})
  survey.name = name
  survey.owner = owner
  survey.active = active
  survey.multi_selection = multi
  survey.num_tickets = num_tickets
  if survey_id is None:
    db.session.add(survey)
    db.session.commit()

  # TODO: delete existing options
  existing_ops = Option.query.filter_by(survey_id=survey.id)
  map(lambda op: db.session.delete(op), existing_ops)

  # Insert options
  for op in options:
    option = Option()
    option.content = op
    option.survey_id = survey.id
    db.session.add(option)

  db.session.commit()
  return jsonify(survey_id = survey.id)


@app.route('/surveys', defaults={'owner': ''})
@app.route('/surveys/<owner>')
def ListSurveys(owner):
  if owner:
    surveys = Survey.query.filter_by(owner=owner).all()
  else:
    surveys = Survey.query.all()
  if not surveys:
    return jsonify()
  survey_pojs = []
  for survey in surveys:
    survey_poj = survey.to_poj()

    options = Option.query.filter_by(survey_id=survey.id)
    survey_poj['options'] = [op.to_poj() for op in options]

    selections = Selection.query.filter_by(survey_id=survey.id)
    owner_selection = defaultdict(list)
    for s in selections:
      owner_selection[s.owner].append(s.option_id)
    owner_selection = [
      {'owner': k, 'selections': v} for k, v in owner_selection.iteritems()
    ]
    survey_poj['selections'] = owner_selection

    survey_pojs.append(survey_poj)
  return jsonify({'surveys': survey_pojs})


@app.route('/selection/create/<survey_id>')
def CreateSelection(survey_id):
  owner = request.args.get('owner', '')
  opids = set(request.args.getlist('option_id'))
  if not owner or not owner:
    return jsonify({'error': 'Bad arguments.'})
  existing_opids = set([str(op.id) for op in Option.query.filter_by(survey_id=survey_id)])
  if opids & existing_opids != opids:
    return jsonify({'error': 'Option id doesn\'t exist.'})

  # TODO(zzb): Delete existing selections
  existing_selections = Selection.query.filter_by(survey_id=survey_id, owner=owner)
  for s in existing_selections:
    db.session.delete(s)

  for opid in opids:
    selection = Selection()
    selection.survey_id = survey_id
    selection.owner = owner
    selection.option_id = opid
    db.session.add(selection)
  db.session.commit()

  return jsonify()


if __name__ == '__main__':
  admin.add_view(ModelView(Survey, db.session))
  admin.add_view(ModelView(Option, db.session))
  admin.add_view(ModelView(Selection, db.session))
  app.run('0.0.0.0', debug=True)
