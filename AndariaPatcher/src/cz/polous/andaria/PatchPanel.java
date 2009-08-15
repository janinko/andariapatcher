package cz.polous.andaria;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

/*******************************************************************************
 * Panel containing PatchItem element details.
 * @author  Martin Polehla (andaria_patcher@polous.cz)
 * @version 0.1
 ******************************************************************************/
class PatchPanel extends JPanel {

    Log log;
    private PatchItem patchItem;

    /***************************************************************************
     * Create new panel PatchPanel and inicialize object
     * @param pi PatchItem linked to this instance of object
     **************************************************************************/
    public PatchPanel(PatchItem pi) {
        log = new Log(this);
        patchItem = pi;
        initComponents();

        PatchItemMouseListener cl = new PatchItemMouseListener();

        addMouseListener(cl);
        refresh();
    }

    /***************************************************************************
     * Re-set checkboxes state and panel colour
     **************************************************************************/
    public void refresh() {
        jChAutoInstall.setSelected(patchItem.getAutoInstallFlag());
        jChInstall.setSelected(patchItem.getInstallFlag());
        jChRequired.setSelected(patchItem.isRequired());
        // green 204,255,204 - installed
        // red 255,233,233 - required
        // white 255,255,255 - user choice
        if (patchItem.isRequired()) {
            if (patchItem.isInstalled()) {
                setBg(new Color(204, 255, 204));
            } else {
                setBg(new Color(255, 233, 233));
            }
        } else {
            if (patchItem.getAutoInstallFlag()) {
                if (patchItem.isInstalled()) {
                    setBg(new Color(204, 255, 204));
                } else {
                    setBg(new Color(255, 233, 233));
                }
            } else {
                setBg(new Color(255, 255, 255));
            }
        }


    }

    /***************************************************************************
     * Set background colour of PatchItem panel. It should change colour of
     * every visible element of panel.
     * @param c New colour.
     **************************************************************************/
    private void setBg(Color c) {
        // green 204,255,204 - installed
        // red 255,233,233 - required
        // white 255,255,255 - user choice

        setBackground(c);
        jChRequired.setBackground(c);
        jChInstall.setBackground(c);
        jLSize.setBackground(c);

        jSeparator1.setBackground(c);
        jSeparator2.setBackground(c);
        jSeparator3.setBackground(c);
        jSeparator1.setForeground(c);
        jSeparator2.setForeground(c);
        jSeparator3.setForeground(c);

        jChAutoInstall.setBackground(c);

        jTSize.setBackground(c);

    }

    /***************************************************************************
     * Set jChWanted checkbox state.
     * @param b new state of checkbox
     **************************************************************************/
    //public void setWanted(boolean wanted) { jChInstallFlag.setSelected( wanted ); }
    /***************************************************************************
     * Generate toolTip text for jPanel tooltip property.
     * @return a tooltip string value
     **************************************************************************/
    public String getToolTip() {

        return "<html><p><b>Jméno:</b> " + patchItem.getName() + "</p><p><b>Soubor:</b> " + patchItem.getFileName() + "</p><p><b>Verze:</b> " + patchItem.getVersions() + "</p><p><b>Datum vydání:</b> " + patchItem.getDates() + "</p><p><b>Kontrolní součet:</b> " + patchItem.getHash() + "</p><br><p><i>" + patchItem.getDescription() + "</i></p></html>";
    }

    /**
     * Implements item selection process.
     * When any elements of form sends onMouseClicked event, PatchItemMouseListener object
     * should switch "wanted" state of it's patchItem and switch state of
     * jChWanted checkbox.
     */
    class PatchItemMouseListener
            implements MouseListener {

        PatchItemMouseListener() {
            jChRequired.addMouseListener(this);
            //jChInstall.addMouseListener(this);

            jLSize.addMouseListener(this);
            jTSize.addMouseListener(this);

            jSeparator1.addMouseListener(this);
            jSeparator2.addMouseListener(this);
            jSeparator3.addMouseListener(this);

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            JComponent jc = (JComponent) e.getComponent();
            Action action = jc.getActionMap().get("postTip");

            if (action != null) {
                action.actionPerformed(new ActionEvent(jc, ActionEvent.ACTION_PERFORMED, "postTip"));
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            log.addDebug("Click: ".concat(patchItem.getName()));
            patchItem.switchInstallFlag();
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jChInstall = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jChRequired = new javax.swing.JCheckBox();
        jSeparator3 = new javax.swing.JSeparator();
        jChAutoInstall = new javax.swing.JCheckBox();
        jTSize = new javax.swing.JTextField();
        jLSize = new javax.swing.JLabel();

        setBackground(java.awt.Color.white);
        setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(163, 125, 86), 1, true), patchItem.getName(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14), new java.awt.Color(163, 125, 86))); // NOI18N
        setForeground(new java.awt.Color(163, 125, 86));
        setToolTipText(getToolTip());
        setFocusable(false);
        setMinimumSize(new java.awt.Dimension(510, 50));
        setPreferredSize(new java.awt.Dimension(510, 50));

        jChInstall.setBackground(getBackground());
        jChInstall.setForeground(getForeground());
        jChInstall.setSelected(true);
        jChInstall.setText("Instalovat");
        jChInstall.setToolTipText(getToolTipText());
        jChInstall.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jChInstall.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jChInstall.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jChInstallStateChanged(evt);
            }
        });
        jChInstall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jChInstallActionPerformed(evt);
            }
        });

        jSeparator1.setBackground(getBackground());
        jSeparator1.setForeground(getBackground());
        jSeparator1.setToolTipText(getToolTipText());
        jSeparator1.setMinimumSize(new java.awt.Dimension(10, 15));
        jSeparator1.setPreferredSize(new java.awt.Dimension(10, 15));

        jSeparator2.setBackground(getBackground());
        jSeparator2.setForeground(getBackground());
        jSeparator2.setToolTipText(getToolTipText());
        jSeparator2.setMinimumSize(new java.awt.Dimension(10, 15));
        jSeparator2.setPreferredSize(new java.awt.Dimension(10, 15));

        jChRequired.setBackground(getBackground());
        jChRequired.setForeground(getForeground());
        jChRequired.setSelected(true);
        jChRequired.setToolTipText(getToolTipText());
        jChRequired.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jChRequired.setEnabled(false);
        jChRequired.setFocusable(false);
        jChRequired.setLabel("Povinný");
        jChRequired.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jChRequired.setMaximumSize(new java.awt.Dimension(50, 15));
        jChRequired.setMinimumSize(new java.awt.Dimension(50, 15));
        jChRequired.setPreferredSize(new java.awt.Dimension(50, 15));

        jSeparator3.setBackground(getBackground());
        jSeparator3.setForeground(getBackground());
        jSeparator3.setToolTipText(getToolTipText());
        jSeparator3.setMinimumSize(new java.awt.Dimension(10, 15));
        jSeparator3.setPreferredSize(new java.awt.Dimension(10, 15));

        jChAutoInstall.setBackground(getBackground());
        jChAutoInstall.setForeground(getForeground());
        jChAutoInstall.setSelected(true);
        jChAutoInstall.setText("Automaticky");
        jChAutoInstall.setToolTipText(getToolTipText());
        jChAutoInstall.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jChAutoInstall.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jChAutoInstall.setMaximumSize(new java.awt.Dimension(50, 15));
        jChAutoInstall.setMinimumSize(new java.awt.Dimension(50, 15));
        jChAutoInstall.setPreferredSize(new java.awt.Dimension(50, 15));
        jChAutoInstall.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jChAutoInstallStateChanged(evt);
            }
        });
        jChAutoInstall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jChAutoInstallActionPerformed(evt);
            }
        });

        jTSize.setBackground(getBackground());
        jTSize.setEditable(false);
        jTSize.setForeground(getForeground());
        jTSize.setText(( patchItem.getSize()>1024 ?
            ( patchItem.getSize()>1048576 ?
                Long.toString(patchItem.getSize()/1048576) + " MB" :
                Long.toString(patchItem.getSize()/1024) + " kB"
            ) :
            Long.toString(patchItem.getSize()) + " B"
        ));
        jTSize.setToolTipText(getToolTipText());
        jTSize.setBorder(null);
        jTSize.setMaximumSize(new java.awt.Dimension(80, 15));
        jTSize.setMinimumSize(new java.awt.Dimension(80, 15));

        jLSize.setBackground(getBackground());
        jLSize.setForeground(getForeground());
        jLSize.setText("Velikost: ");
        jLSize.setToolTipText(getToolTipText());

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jChInstall)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jChRequired, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLSize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 58, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTSize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jChAutoInstall, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jChInstall)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jChRequired, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jChAutoInstall, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jTSize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLSize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jChAutoInstallStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jChAutoInstallStateChanged
       
    }//GEN-LAST:event_jChAutoInstallStateChanged

    private void jChInstallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jChInstallActionPerformed
        // TODO add your handling code here:
        log.addDebug("Akce: ".concat(patchItem.getName()));
        patchItem.setInstallFlag(jChInstall.isSelected());
    }//GEN-LAST:event_jChInstallActionPerformed

    private void jChInstallStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jChInstallStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jChInstallStateChanged

    private void jChAutoInstallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jChAutoInstallActionPerformed

        patchItem.setAutoInstallFlag(jChAutoInstall.isSelected());
    }//GEN-LAST:event_jChAutoInstallActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jChAutoInstall;
    private javax.swing.JCheckBox jChInstall;
    private javax.swing.JCheckBox jChRequired;
    private javax.swing.JLabel jLSize;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField jTSize;
    // End of variables declaration//GEN-END:variables
}
