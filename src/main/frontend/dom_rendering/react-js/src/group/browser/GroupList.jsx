import React, { Component, Fragment } from 'react';
import GroupCard from './GroupCard';
import Loader from 'components/Loader';

class GroupList extends Component {
    constructor(props) {
        super(props);
        this.maxHeight = -1;
        this.updates = {};
        this.style = {};
        this.updateHeight = this.updateHeight.bind(this);
    }

    getHeight() {
        return this.el.clientHeight;
    }

    getWidth() {
        return this.el.clientWidth;
    }

    /**
     * Vrati handler pre aktualizovanie vysok
     * @param {number} index index stlpca v riadku 
     */
    updateHeight(index) {
        // Vytvori anonymnu metodu
        return (height) => {
            // Vyberieme si urcite hodnoty z vlastnosti komponentu
            const { recomputeHeight, memberships } = this.props;

            // Porovna a vyberie vacsiu vysku
            this.maxHeight = Math.max(this.maxHeight, height);
            // Zaznamena vysku
            this.updates[index] = height;

            // Pomocne premenne
            const updateCount = Object.keys(this.updates).length;
            const heights = Object.values(this.updates);

            // Podmienka, ktora sa splni: 
            // ak doslo k poctu aktualizacii kolko je stlpcov
            // a
            // ak nejaka vyska komponentu sa nezhoduje s najvacsou
            if (updateCount === memberships.size && heights.some(height => height !== this.maxHeight)) {
                // Nastavi styl, presnejsie vysku pre komponenety
                this.style = { height: this.maxHeight };
                // Spusti porepocitavanie vysky v rodicovi
                this.props.recomputeHeight();
                // Vycisti zaznam aktualizacii
                this.updates = {};
            }
        }
    }

    getOptimalDiameterForLoader() {
        if (this.el == null) {
            return 0;
        }
        //Iba nahoda
        return this.props.style.height * 0.9;
    }

    render() {
        const { memberships, loaded, style, updateHeight, onDelete, acceptInvite, recomputeHeight, columnCount } = this.props;
        let content;

        if (loaded) {
            content = memberships.map((membership, index) =>
                <GroupCard
                    key={membership._links.self.href}
                    membership={membership}
                    onDelete={() => onDelete(membership)}
                    onDeclineInvite={() => onDelete(membership)}
                    onAcceptInvite={() => acceptInvite(membership)}
                    updateParent={this.updateHeight(index)}
                    style={this.style}
                />
            );
            content = content.withMutations(list => {
                for (let index = list.size; index < columnCount; index++) {
                    list.push(<div key={index} className="blank-card" />);
                }
            });
        }
        else {
            content = (
                <Loader
                    className="article-loader"
                    loaderSize="10px"
                    diameter={this.getOptimalDiameterForLoader() + 'px'}
                    animationSpeed="0.9s"
                    animationType="linear"
                    text="Loading ..."
                />
            );
        }

        return (
            <div ref={el => this.el = el} className="group-list" style={style}>
                <div className="group-card-container">
                    {content}
                </div>
            </div>
        );

    }
}

export default GroupList;