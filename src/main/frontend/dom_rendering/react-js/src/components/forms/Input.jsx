import React, { Fragment } from 'react';
import PropTypes from 'prop-types';
import ReactDOM from 'react-dom';
import { formatClassName } from 'util/ClassFormatter';
import { List } from 'immutable';
import InputWrapper from './InputWrapper';
import {defaultTransformErrors, defaultValidation} from './Validation';

export default class Input extends React.PureComponent {
    constructor(props) {
        super(props);

        this.state = {
            errors: List(),
            hasBeenValidated: false,
            showValidation: true,
            processing: false
        };

        // this.onChange = this.onChange.bind(this);
        this.validate = this.validate.bind(this);
        this.reset = this.reset.bind(this);
        this.isValid = this.isValid.bind(this);
        this.getValue = this.getValue.bind(this);
        this.setValue = this.setValue.bind(this);
        
        this.sync = () => this.value = ReactDOM.findDOMNode(this.input).value;
        this.setProcessing = (value) => this.setState({processing: value});

        this.value = props.value;
    }

    getValue() {
        if (!this.props.parse) {
            return this.value;
        }
        return this.props.parse(this.value);
    }

    setValue(value) {
        ReactDOM.findDOMNode(this.input).value = value;
    }

    setErrors(errors, callback) {
        this.setState({
            errors: List(errors)
        }, callback);
    }

    reset() {
        this.setState({
            errors: List(),
            hasBeenValidated: false,
            processing: false,
        });
        this.setValue('');
    }

    validate(showValidation = true) {
        const errors = List(defaultTransformErrors(defaultValidation(this.value, this.props), this.props));
        this.setState({hasBeenValidated: true, showValidation: showValidation, errors: errors});
        return errors;
    }

    // onChange(event) {
    //     const target = event.target;
    //     const value = target.type === 'checkbox' ? target.checked : target.value;
    //     this.setState({
    //         value: value,
    //         errors: List()
    //     });
    // }

    isValid() {
        return this.state.errors.isEmpty();
    }

    canShowValidation() {
        return this.state.showValidation && this.state.hasBeenValidated;
    }

    getClassName() {
        const { hidden, focused } = this.props;
        const base = formatClassName('form-control', hidden ? 'd-none' : '');
        if (!this.canShowValidation()) {
            return base;
        }

        return formatClassName(base, this.isValid() ? 'is-valid' : 'is-invalid');
    }

    createChild(child, className) {
        let props = {
            type: this.props.type,
            processing: this.state.processing,
            disabled: this.props.disabled,
            inputClassName: className,
            placeholder: this.props.placeholder,
            defaultValue: this.props.value,
            inputRef: input => this.input = input
        };

        return React.cloneElement(child, props);
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.value !== this.props.value) {
            this.setValue(nextProps.value);
        }
    }

    componentDidMount() {
        this.props.form.addInput(this);
        this.setValue(this.props.value);
    }

    componentWillUnmount(){
        this.props.form.removeInput(this);
    }

    render() {
        const { name, placeholder, type, label, help, hidden, children, required, className, focused, disabled, ref } = this.props;
        const { errors, value, processing } = this.state;

        const inputClassName = this.getClassName();

        return (
            <div className={formatClassName(className, hidden ? 'd-none' : '')}>
                {label &&
                    <label htmlFor={name}>{label}{required && <span className="text-danger"> *</span>}</label>
                }
                {!children &&
                    <InputWrapper
                        type={type} name={name} id={name} inputClassName={inputClassName}
                        placeholder={placeholder} inputRef={input => this.input = input} value={value}
                        required={required} disabled={processing || disabled}/>
                }
                {children &&
                    <Fragment>
                        {this.createChild(React.Children.only(children), inputClassName)}
                    </Fragment>
                }
                {help &&
                    <small className="form-text text-muted">{help}</small>
                }
                {!this.isValid() &&
                    <section className={!this.canShowValidation() ? 'd-none' : null}>
                        {errors.map(err => <small key={err} className="form-text text-danger">{err}</small>)}
                    </section>
                }
                {this.isValid() &&
                    <small className={formatClassName('form-text text-success', !this.canShowValidation() ? 'd-none' : '')}>Ok <i className="fa fa-check"></i></small>
                }
            </div>
        );
    }
}

Input.defaultProps = {
    placeholder: null,
    value: '',
    required: false,
    type: 'text',
    label: null,
    help: null,
    className: 'form-group',
    min: 0,
    max: Infinity,
    pattern: null,
    patternHelp: null,
    hidden: false,
    validation: (value) => null,
    transformError: (error) => error
};

Input.propTypes = {
    name: PropTypes.string.isRequired
};