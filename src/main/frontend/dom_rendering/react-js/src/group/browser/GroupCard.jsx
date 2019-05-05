import React, { Fragment } from 'react';
import ConfirmModal from 'components/ConfirmModal';
import Loader from 'components/Loader';

export default class GroupCard extends React.PureComponent {
    constructor(props) {
        super(props);
        this.state = {
            open: false,
            deleting: false
        };
        this.toggleOpen = this.toggleOpen.bind(this);
        this.handleDelete = this.handleDelete.bind(this);
    }

    getHeight() {
        return this.el.clientHeight;
    }

    getWidth() {
        return this.el.clientWidth;
    }

    toggleOpen(value) {
        return () => {
            this.setState({ open: value });
        }
    }

    handleDelete() {
        this.setState({ deleting: true });
        this.props.onDelete().catch(error => {
            console.error(error);
            this.setState({ deleting: false });
        });
    }

    // Zavola sa po vyrenderovani komponentu
    componentDidMount() {
        // Posli vysku GroupListu
        this.props.updateParent(this.el.offsetHeight, this);
    }

    // Vola sa pri kazdej aktualizacii
    componentDidUpdate() {
        // Ak nahodou nemame referenciu na element, nevykonaj ziadnu akciu
        if (!this.el) {
            return;
        }
        // Ak referencia existuje, element aktualizuje GroupList s aktualnou vyskou
        this.props.updateParent(this.el.offsetHeight, this);
    }

    render() {
        const { loaded, membership, style, onAcceptInvite, onDeclineInvite } = this.props;
        const group = membership.group;
        const user = membership.user;

        if (this.state.deleting) {
            return (
                <Loader
                    className="article-loader"
                    loaderSize="10px"
                    diameter={style.height * 0.9 + 'px'}
                    animationSpeed="0.9s"
                    animationType="linear"
                    text="Odstraňujem ..."
                />
            )
        }

        return (
            <Fragment>
                {this.state.open &&
                    <ConfirmModal text={`Ste si istý, že chcete opustiť skupinu ${group.name} ?`} open={this.state.open}
                        header="Opustiť skupinu" onClose={this.toggleOpen(false)} action={this.handleDelete} />
                }
                <div ref={el => this.el = el} className="card group-card" style={style}>
                    <div className="card-body">
                        {membership.invited &&
                            <small className="text-muted">Pozvánka</small>
                        }
                        <h5 className="card-title text-primary">{group.name}</h5>
                        <p className="card-text">{group.description}</p>
                    </div>
                    <div className="card-footer text-muted d-flex">
                        {!membership.invited &&
                            <Fragment>
                                <a href={`/group/${group.name}/`} className="card-link my-auto">Pozrieť</a>
                                <button className="card-link btn btn-link" onClick={this.toggleOpen(true)}>Opustiť</button>
                            </Fragment>
                        }
                        {membership.invited &&
                            <Fragment>
                                <button className="card-link btn btn-link text-success" onClick={onAcceptInvite}>Prijať <i className="fa fa-lg fa-check"></i></button>
                                <button className="card-link btn btn-link text-danger" onClick={onDeclineInvite}>Odmietnuť <i className="fa fa-lg fa-times"></i></button>
                            </Fragment>
                        }
                    </div>
                </div>
            </Fragment>
        );
    }
}